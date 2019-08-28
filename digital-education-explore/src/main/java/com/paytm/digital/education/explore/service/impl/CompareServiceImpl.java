package com.paytm.digital.education.explore.service.impl;

import static com.mongodb.QueryOperators.OR;
import static com.paytm.digital.education.explore.constants.CompareConstants.ACRES;
import static com.paytm.digital.education.explore.constants.CompareConstants.AS_PER;
import static com.paytm.digital.education.explore.constants.CompareConstants.AVERAGE;
import static com.paytm.digital.education.explore.constants.CompareConstants.CAREERS360;
import static com.paytm.digital.education.explore.constants.CompareConstants.DETAILS;
import static com.paytm.digital.education.explore.constants.CompareConstants.INST_LATEST_YEAR;
import static com.paytm.digital.education.explore.constants.CompareConstants.MAX;
import static com.paytm.digital.education.explore.constants.CompareConstants.MEDIAN;
import static com.paytm.digital.education.explore.constants.CompareConstants.MIN;
import static com.paytm.digital.education.explore.constants.CompareConstants.NA_SIGN;
import static com.paytm.digital.education.explore.constants.CompareConstants.NIRF;
import static com.paytm.digital.education.explore.constants.CompareConstants.RANKED;
import static com.paytm.digital.education.explore.constants.CompareConstants.RANKINGS;
import static com.paytm.digital.education.explore.constants.CompareConstants.YES;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_SHORT_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.constant.ExploreConstants.OFFICIAL_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.SUBEXAM_ID;
import static com.paytm.digital.education.enums.EducationEntity.INSTITUTE;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.database.entity.Course;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.entity.Placement;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.dto.OfficialAddress;
import com.paytm.digital.education.explore.response.dto.detail.CompareDetail;
import com.paytm.digital.education.explore.response.dto.detail.CompareInstDetail;
import com.paytm.digital.education.explore.response.dto.detail.CompareRanking;
import com.paytm.digital.education.explore.response.dto.detail.Ranking;
import com.paytm.digital.education.explore.service.CompareService;
import com.paytm.digital.education.explore.service.helper.FacilityDataHelper;
import com.paytm.digital.education.explore.service.helper.StreamDataHelper;
import com.paytm.digital.education.explore.service.helper.SubscriptionDetailHelper;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.digital.education.explore.utility.CompareUtil;
import com.paytm.digital.education.mapping.ErrorEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CompareServiceImpl implements CompareService {

    private static Map<Long, String>          parentInstituteNameMap = new HashMap<>();
    private static Map<Long, List<Course>>    instituteCoursesMap    = new HashMap<>();
    private        InstituteDetailServiceImpl instituteDetailService;
    private        CommonMongoRepository      commonMongoRepository;
    private        StreamDataHelper           streamDataHelper;
    private        FacilityDataHelper         facilityDataHelper;
    private        CompareInsightsServiceImpl compareInsightsService;
    private        SubscriptionDetailHelper   subscriptionDetailHelper;

    @Override
    public CompareDetail compareInstitutes(Map<Long, String> instKeyMap, String fieldGroup,
            List<String> fields, Long userId) throws IOException, TimeoutException {

        CompareDetail compareDetail = new CompareDetail();
        List<CompareInstDetail> instituteDetailsList = new ArrayList<>();
        List<String> fieldGroupList = getFieldGroups(fieldGroup);
        if (!CollectionUtils.isEmpty(instKeyMap)) {
            List<Long> instList = new ArrayList<>(instKeyMap.keySet());
            List<Institute> institutes =
                    instituteDetailService.getInstitutes(instList, fieldGroupList);
            for (Institute institute : institutes) {
                if (!instKeyMap.get(institute.getInstituteId()).equals(CommonUtil
                        .convertNameToUrlDisplayName(institute.getOfficialName()))) {
                    throw new BadRequestException(ErrorEnum.INVALID_INSTITUTE_NAME,
                            ErrorEnum.INVALID_INSTITUTE_NAME.getExternalMessage());
                }
            }
            updateParentInstituteNameMap(institutes);
            updateInstituteCoursesMap(instList);
            Map<Long, Institute> instituteMap =
                    institutes.stream().collect(Collectors.toMap(i -> i.getInstituteId(), i -> i));

            List<Institute> finalInstituteList = new ArrayList<>();
            for (Long instId : instList) {
                if (Objects.nonNull(instituteMap.get(instId))) {
                    finalInstituteList.add(instituteMap.get(instId));
                }
            }

            Map<String, List<String>> keyInsights =
                    compareInsightsService.getInstituteKeyInsights(finalInstituteList);
            if (!CollectionUtils.isEmpty(keyInsights)) {
                compareDetail.setKeyInsights(keyInsights);
            }

            for (Institute institute : finalInstituteList) {
                CompareInstDetail instDetail = getCompareInstDetail(institute);
                instituteDetailsList.add(instDetail);
            }
        }

        if (!CollectionUtils.isEmpty(instituteDetailsList)) {
            updateRankings(instituteDetailsList);
            updatePlacememnts(instituteDetailsList);
            updateShortist(instituteDetailsList, INSTITUTE, userId);

            compareDetail.setInstitutes(instituteDetailsList);
        }
        return compareDetail;
    }

    public List<Exam> getExamList(Set<Long> examIds) {
        List<String> examFields = Arrays.asList(EXAM_SHORT_NAME, EXAM_ID, SUBEXAM_ID);
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(EXAM_ID, new ArrayList<>(examIds));
        queryObject.put(SUBEXAM_ID, new ArrayList<>(examIds));
        List<Exam> examList = commonMongoRepository
                .findAll(queryObject, Exam.class, examFields, OR);
        return examList;
    }

    private void updateInstituteCoursesMap(List<Long> institutes) {
        List<String> courseFields = commonMongoRepository.getFieldsByGroup(Course.class, DETAILS);
        List<Course> courses = new ArrayList<>();
        if (!CollectionUtils.isEmpty(courseFields)) {
            courses = commonMongoRepository
                    .getEntityFieldsByValuesIn(INSTITUTE_ID, institutes, Course.class,
                            courseFields);
        }

        instituteCoursesMap = courses.stream().filter(c -> Objects.nonNull(c.getInstitutionId()))
                .collect(Collectors.groupingBy(c -> c.getInstitutionId(),
                        Collectors.mapping(c -> c, Collectors.toList())));

    }

    private void updateParentInstituteNameMap(List<Institute> institutes) {
        String parentInstitutionName = null;
        List<String> parentInstitutionFields = new ArrayList<>();
        parentInstitutionFields.add(OFFICIAL_NAME);
        parentInstitutionFields.add(INSTITUTE_ID);

        List<Long> parentInstitutionIds =
                institutes.stream().filter(i -> Objects.nonNull(i.getParentInstitution()))
                        .map(i -> i.getParentInstitution()).collect(Collectors.toList());

        List<Institute> parentInstitutions = commonMongoRepository
                .getEntityFieldsByValuesIn(INSTITUTE_ID, parentInstitutionIds, Institute.class,
                        parentInstitutionFields);

        Set<Institute> uniqueInstitutions = new HashSet<>(parentInstitutions);
        parentInstituteNameMap = uniqueInstitutions.stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(i -> i.getInstituteId(), i -> i.getOfficialName()));
    }

    private List<String> getFieldGroups(String fieldGroup) {
        return commonMongoRepository.getFieldsByGroup(Institute.class, fieldGroup);
    }

    private void updatePlacememnts(List<CompareInstDetail> instituteDetailsList) {

        for (CompareInstDetail c : instituteDetailsList) {
            Map<String, Placement> p = c.getFakePlacements();

            if (Objects.nonNull(p)) {
                if (p.containsKey(MEDIAN)) {
                    c.setPlacement(p.get(MEDIAN));
                    continue;
                } else if (p.containsKey(AVERAGE)) {
                    c.setPlacement(p.get(AVERAGE));
                    continue;
                } else if (p.containsKey(MAX)) {
                    c.setPlacement(p.get(MAX));
                    continue;
                } else if (p.containsKey(MIN)) {
                    c.setPlacement(p.get(MIN));
                }
            }
        }
    }

    // find and update latest overall ranking for NIRF and Careers360
    private void updateRankings(List<CompareInstDetail> instituteDetailsList) {
        for (CompareInstDetail c : instituteDetailsList) {
            Map<String, Ranking> rankingMap = c.getFakeRankings();
            if (Objects.nonNull(rankingMap)) {
                for (Map.Entry<String, Ranking> en : rankingMap.entrySet()) {
                    String source = en.getKey();
                    Ranking ranking = en.getValue();

                    if (Objects.nonNull(ranking)) {
                        CompareRanking cr = new CompareRanking();
                        if (source.equalsIgnoreCase(NIRF)) {
                            cr.setTitle(RANKED + ranking.getRank());
                        } else if (source.equalsIgnoreCase(CAREERS360)) {
                            if (Objects.nonNull(ranking.getRank())) {
                                cr.setTitle(RANKED + ranking.getRank());
                            } else if (Objects.nonNull(ranking.getRating())) {
                                cr.setTitle(RANKED + ranking.getRating());
                            }
                        }
                        cr.setSource(ranking.getSource());
                        cr.setSubtitle(AS_PER + ranking.getSource() + RANKINGS);

                        Set<CompareRanking> crList =
                                new TreeSet<>((c1, c2) -> c2.getSource().compareTo(c1.getSource()));
                        if (!CollectionUtils.isEmpty(c.getRankings())) {
                            crList.addAll(c.getRankings());
                        }
                        if (Objects.isNull(crList)) {
                            crList = new HashSet<>();
                        }
                        if (Objects.nonNull(ranking.getRank()) || Objects
                                .nonNull(ranking.getRating())) {
                            crList.add(cr);
                        }
                        if (!CollectionUtils.isEmpty(crList)) {
                            c.setRankings(crList);
                        }
                    }

                }
            }
        }
    }

    private CompareInstDetail getCompareInstDetail(Institute inst) {
        CompareInstDetail cDetail = new CompareInstDetail();

        cDetail.setInstituteId(inst.getInstituteId());
        cDetail.setInstituteName(inst.getOfficialName());
        cDetail.setUrlDisplayKey(CommonUtil.convertNameToUrlDisplayName(inst.getOfficialName()));
        if (Objects.nonNull(inst.getCampusSize())) {
            cDetail.setCampusArea(inst.getCampusSize() + ACRES);
        }
        if (Objects.nonNull(inst.getStudentCount()) && inst.getStudentCount() != 0) {
            cDetail.setTotalIntake(inst.getStudentCount());
        }
        String parentInstitutionName = parentInstituteNameMap.get(inst.getParentInstitution());
        cDetail.setApprovals(getApprovalDetail(inst.getApprovals(),
                Objects.nonNull(parentInstitutionName) ? parentInstitutionName : ""));
        if (Objects.nonNull(inst.getFacilities())) {
            cDetail.setFacilities(getFacilitiesDetail(inst.getFacilities()));
        } else {
            cDetail.setFacilities(getFacilitiesDetail(new ArrayList<>()));
        }
        cDetail.setFakeRankings(CompareUtil.getResponseRankingMap(inst.getRankings()));
        if (Objects.nonNull(inst.getSalariesPlacement())) {
            cDetail.setFakePlacements(getPlacements(inst.getSalariesPlacement()));
        }
        List<Course> courses = getCourses(inst.getInstituteId());
        if (!CollectionUtils.isEmpty(courses)) {
            Map<Long, String> courseMap = getCourseMap(courses);
            if (!CollectionUtils.isEmpty(courseMap)) {
                cDetail.setCourses(courseMap);
            }

            cDetail.setCourseLevel(getCourseLevel(courses));
            cDetail.setExamsAccepted(getExamAccepted(courses));
            cDetail.setMinimumCourseFee(CompareUtil.getMinCourseFee(courses));
            cDetail.setStreamsPreparedFor(getStreams(courses));
        }
        if (Objects.nonNull(inst.getGallery()) && Objects.nonNull(inst.getGallery().getLogo())) {
            cDetail.setLogo(CommonUtil.getLogoLink(inst.getGallery().getLogo(), INSTITUTE));
        }
        OfficialAddress officialAddress =
                CommonUtil.getOfficialAddress(inst.getInstitutionState(),
                        inst.getInstitutionCity(), inst.getPhone(), inst.getUrl(),
                        inst.getOfficialAddress());
        cDetail.setOfficialAddress(officialAddress);
        return cDetail;
    }

    private Map<Long, String> getCourseMap(List<Course> courses) {
        return courses.stream().filter(c -> Objects.nonNull(c.getCourseNameOfficial()))
                .collect(Collectors.toMap(c -> c.getCourseId(), c -> c.getCourseNameOfficial(),
                    (a1, a2) -> a2));
    }

    private Map<String, Placement> getPlacements(List<Placement> placements) {
        int latest = INST_LATEST_YEAR;
        for (Placement p : placements) {
            if (p.getYear() > latest) {
                latest = p.getYear();
            }
        }
        Map<String, Placement> placementMap = new HashMap<>();

        for (Placement p : placements) {
            if (p.getYear() == latest) {
                if (p.getMedian() != null) {
                    p.setAverage(null);
                    p.setMaximum(null);
                    p.setMinimum(null);
                    updatePlacementMap(placementMap, MEDIAN, p);
                } else if (p.getAverage() != null) {
                    p.setMaximum(null);
                    p.setMinimum(null);
                    updatePlacementMap(placementMap, AVERAGE, p);
                } else if (p.getMaximum() != null) {
                    p.setMinimum(null);
                    updatePlacementMap(placementMap, MAX, p);
                } else if (p.getMinimum() != null) {
                    updatePlacementMap(placementMap, MIN, p);
                }
            }
        }
        if (!CollectionUtils.isEmpty(placementMap)) {
            return placementMap;
        }
        return null;

    }

    private void updatePlacementMap(Map<String, Placement> placementMap, String pGroup,
            Placement p) {
        Placement pl = placementMap.get(pGroup);
        if (Objects.nonNull(pl)) {
            switch (pGroup) {
                case MEDIAN:
                    if (p.getMedian() > pl.getMedian()) {
                        placementMap.put(pGroup, p);
                    }
                    break;
                case AVERAGE:
                    if (p.getAverage() > pl.getAverage()) {
                        placementMap.put(pGroup, p);
                    }
                    break;
                case MAX:
                    if (p.getMaximum() > pl.getMaximum()) {
                        placementMap.put(pGroup, p);
                    }
                    break;
                case MIN:
                    if (p.getMinimum() > pl.getMinimum()) {
                        placementMap.put(pGroup, p);
                    }
                    break;
                default:
                    break;
            }
        } else {
            placementMap.put(pGroup, p);
        }
    }

    private Set<String> getStreams(List<Course> courses) {
        Map<String, String> streamMap = streamDataHelper.getStreamMap();
        Set<String> streamList = courses.stream().filter(c -> Objects.nonNull(c.getStreams()))
                .flatMap(c -> c.getStreams().stream())
                .filter(st -> Objects.nonNull(streamMap.get(st.toLowerCase())))
                .map(st -> streamMap.get(st.toLowerCase()))
                .collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(streamList)) {
            return streamList;
        }
        return null;
    }

    private List<String> getExamAccepted(List<Course> courses) {
        Set<Long> examAccepted = courses.stream().filter(c -> Objects.nonNull(c.getExamsAccepted()))
                .flatMap(c -> c.getExamsAccepted().stream())
                .collect(Collectors.toSet());

        List<Exam> exams = getExamList(examAccepted);
        List<String> retList =
                exams.stream().filter(e -> Objects.nonNull(e.getExamShortName()))
                        .map(e -> e.getExamShortName()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(retList)) {
            return retList;
        }
        return null;
    }

    private Set<String> getCourseLevel(List<Course> courses) {
        Set<String> courseLevel = courses.stream().filter(c -> Objects.nonNull(c.getCourseLevel()))
                .map(c -> c.getCourseLevel().getDisplayName())
                .collect(Collectors.toSet());
        return courseLevel;
    }

    private List<Course> getCourses(Long instId) {
        List<Course> courses = new ArrayList<>();
        if (Objects.nonNull(instituteCoursesMap.get(instId))) {
            courses = instituteCoursesMap.get(instId);
        }
        return courses;
    }


    private List<String> getApprovalDetail(List<String> approvals, String parentInstitutionName) {
        Map<String, String> approvalMap = CommonUtil.getApprovals(approvals, parentInstitutionName);
        List<String> approvalList = null;
        if (Objects.nonNull(approvalMap)) {
            approvalList =
                    approvalMap.entrySet().stream().map(en -> en.getKey() + " " + en.getValue())
                            .collect(Collectors.toList());
        }
        return approvalList;
    }

    private Map<String, String> getFacilitiesDetail(List<String> facilities) {
        Map<String, String> facilityMap = facilityDataHelper.getFacilitiesMasterList();
        Map<String, String> facMap = facilityMap.entrySet().stream()
                .collect(Collectors.toMap(en -> en.getValue(), en -> facilities
                        .contains(en.getKey().toUpperCase()) ? YES : NA_SIGN));
        if (!CollectionUtils.isEmpty(facMap)) {
            return facMap;
        }
        return null;
    }

    private void updateShortist(List<CompareInstDetail> instDetailList,
            EducationEntity educationEntity, Long userId) {
        List<Long> instituteIds =
                instDetailList.stream().map(instDetail -> instDetail.getInstituteId())
                        .collect(Collectors.toList());
        List<Long> subscribedEntities = subscriptionDetailHelper
                .getSubscribedEntities(educationEntity, userId, instituteIds);
        if (!CollectionUtils.isEmpty(subscribedEntities)) {
            for (CompareInstDetail compareInstDetail : instDetailList) {
                if (subscribedEntities.contains(compareInstDetail.getInstituteId())) {
                    compareInstDetail.setShortlisted(true);
                }
            }
        }
    }
}
