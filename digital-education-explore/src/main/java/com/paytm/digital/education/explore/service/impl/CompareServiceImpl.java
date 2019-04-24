package com.paytm.digital.education.explore.service.impl;


import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.CourseFee;
import com.paytm.digital.education.explore.database.entity.Placement;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.response.dto.detail.CompareInstDetail;
import com.paytm.digital.education.explore.response.dto.detail.CompareDetail;
import com.paytm.digital.education.explore.response.dto.detail.CompareRanking;
import com.paytm.digital.education.explore.response.dto.detail.Ranking;
import com.paytm.digital.education.explore.service.CompareService;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import com.paytm.digital.education.explore.service.helper.FacilityDataHelper;
import com.paytm.digital.education.explore.service.helper.StreamDataHelper;
import com.paytm.digital.education.explore.utility.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static com.mongodb.QueryOperators.OR;
import static com.paytm.digital.education.explore.constants.ExploreConstants.MEDIAN;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ACRES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.AVERAGE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.MAX;
import static com.paytm.digital.education.explore.constants.ExploreConstants.MIN;
import static com.paytm.digital.education.explore.constants.ExploreConstants.NIRF;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CAREERS360;
import static com.paytm.digital.education.explore.constants.ExploreConstants.RANKED;
import static com.paytm.digital.education.explore.constants.ExploreConstants.AS_PER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.RANKINKS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OFFICIAL_NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.LATEST_YEAR;
import static com.paytm.digital.education.explore.constants.ExploreConstants.GENERAL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_SHORT_NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DETAILS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.YES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.HIPHEN;
import static com.paytm.digital.education.explore.constants.ExploreConstants.UNIVERSITIES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OVERALL_RANKING;

@Slf4j
@Service
public class CompareServiceImpl implements CompareService {

    @Autowired
    private InstituteDetailServiceImpl instituteDetailService;

    @Autowired
    private CommonMongoRepository commonMongoRepository;

    @Autowired
    private StreamDataHelper streamDataHelper;

    @Autowired
    private FacilityDataHelper facilityDataHelper;

    @Override
    public CompareDetail compareInstitutes(List<Long> instList, String fieldGroup,
            List<String> fields) throws IOException, TimeoutException {

        CompareDetail compareDetail = new CompareDetail();
        List<CompareInstDetail> instituteDetailsList = new ArrayList<>();

        for (Long inst : instList) {
            if (Objects.nonNull(inst)) {
                List<String> fieldGroupList = getFieldGroups(fieldGroup);
                Institute institute = instituteDetailService.getInstitute(inst, fieldGroupList);
                CompareInstDetail instDetail = getCompareInstDetail(institute);
                instituteDetailsList.add(instDetail);
            }
        }
        if (!CollectionUtils.isEmpty(instituteDetailsList)) {
            updateRankings(instituteDetailsList);
            updatePlacememnts(instituteDetailsList);

            compareDetail.setInstitutes(instituteDetailsList);
        }

        return compareDetail;
    }

    private List<String> getFieldGroups(String fieldGroup) {
        return commonMongoRepository.getFieldsByGroup(Institute.class, fieldGroup);
    }

    private void updatePlacememnts(List<CompareInstDetail> instituteDetailsList) {

        for (CompareInstDetail c : instituteDetailsList) {
            Map<String, Placement> p = c.getFakePlacements();

            if (Objects.nonNull(p)) {
                if (p.containsKey(MEDIAN)) {
                    c.setPlacements(p.get(MEDIAN));
                    continue;
                } else if (p.containsKey(AVERAGE)) {
                    c.setPlacements(p.get(AVERAGE));
                    continue;
                } else if (p.containsKey(MAX)) {
                    c.setPlacements(p.get(MAX));
                    continue;
                } else if (p.containsKey(MIN)) {
                    c.setPlacements(p.get(MIN));
                }
            }

        }
    }

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
                        cr.setSubtitle(AS_PER + ranking.getSource() + RANKINKS);

                        Set<CompareRanking> crList = c.getRankings();
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
        if (Objects.nonNull(inst.getCampusSize())) {
            cDetail.setCampusArea(inst.getCampusSize() + ACRES);
        }
        cDetail.setTotalIntake(inst.getTotalIntake());
        String parentInstitutionName = getParentInstituteName(inst.getParentInstitution());
        cDetail.setApprovals(getApprovalDetail(inst.getApprovals(), parentInstitutionName));
        cDetail.setFacilities(getFacilitiesDetail(inst.getFacilities()));
        cDetail.setFakeRankings(getResponseRankingMap(inst.getRankings()));
        cDetail.setFakePlacements(getPlacements(inst.getSalariesPlacement()));
        List<Course> courses = getCourses(inst.getInstituteId());
        if (!CollectionUtils.isEmpty(courses)) {
            cDetail.setCourseLevel(getCourseLevel(courses));
            cDetail.setExamAccepted(getExamAccepted(courses));
            cDetail.setMinimumCourseFee(getMinCourseFee(courses));
            cDetail.setStreamsPreparedFor(getStreams(courses));
        }
        return cDetail;
    }

    private Map<String, Placement> getPlacements(List<Placement> placements) {
        int latest = LATEST_YEAR;
        for (Placement p : placements) {
            if (p.getYear() > latest) {
                latest = p.getYear();
            }
        }
        Map<String, Placement> placementMap = new HashMap<>();

        for (Placement p : placements) {
            if (p.getYear() == latest) {
                if (p.getMedian() != null) {
                    updatePlacementMap(placementMap, MEDIAN, p);
                }
                if (p.getAverage() != null) {
                    updatePlacementMap(placementMap, AVERAGE, p);
                }
                if (p.getMaximum() != null) {
                    updatePlacementMap(placementMap, MAX, p);
                }
                if (p.getMinimum() != null) {
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

    private String getParentInstituteName(Long parentInstitutionId) {
        String parentInstitutionName = null;
        List<String> parentInstitutionFields = new ArrayList<>();
        parentInstitutionFields.add(OFFICIAL_NAME);
        if (parentInstitutionId != null) {
            Institute parentInstitution = commonMongoRepository
                    .getEntityByFields(INSTITUTE_ID, parentInstitutionId, Institute.class,
                            parentInstitutionFields);
            parentInstitutionName =
                    parentInstitution != null ? parentInstitution.getOfficialName() : null;
        }
        return parentInstitutionName;
    }

    private Set<String> getStreams(List<Course> courses) {
        Map<String, String> streamMap = streamDataHelper.getStreamMap();
        Set<String> streamList = courses.stream().filter(c -> Objects.nonNull(c.getStreams()))
                .flatMap(c -> c.getStreams().stream())
                .filter(st -> Objects.nonNull(streamMap.get(st.toLowerCase())))
                .map(st -> streamMap.get(st.toLowerCase()))
                .collect(Collectors.toSet());
        return streamList;

    }

    private Long getMinCourseFee(List<Course> courses) {
        Long minFee = Long.MAX_VALUE;
        for (Course c : courses) {
            List<CourseFee> feesList = c.getCourseFees();
            if (!CollectionUtils.isEmpty(feesList)) {
                if (feesList.size() > 1) {
                    for (CourseFee fee : feesList) {
                        if (Objects.nonNull(fee.getCasteGroup()) && fee.getCasteGroup()
                                .equalsIgnoreCase(GENERAL)) {
                            if (Objects.nonNull(fee.getFee())) {
                                minFee = Math.min(minFee, fee.getFee());
                            }
                        }
                    }
                } else {
                    if (feesList.size() == 1) {
                        CourseFee fee = feesList.get(0);
                        if (Objects.nonNull(fee) && Objects.nonNull(fee.getFee())) {
                            minFee = Math.min(minFee, fee.getFee());
                        }
                    }
                }
            }
        }
        if (minFee != Long.MAX_VALUE) {
            return minFee;
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
        return retList;
    }

    private List<Exam> getExamList(Set<Long> examIds) {
        List<String> examFields = new ArrayList<>();
        examFields.add(EXAM_SHORT_NAME);
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(EXAM_ID, new ArrayList<>(examIds));
        List<Exam> examList = commonMongoRepository
                .findAll(queryObject, Exam.class, examFields, OR);
        return examList;
    }

    private Set<String> getCourseLevel(List<Course> courses) {
        Set<String> courseLevel = courses.stream().filter(c -> Objects.nonNull(c.getCourseLevel()))
                .map(c -> c.getCourseLevel().name())
                .collect(Collectors.toSet());
        return courseLevel;
    }

    private List<Course> getCourses(Long instId) {
        List<Course> courses = new ArrayList<>();
        List<String> courseFields = commonMongoRepository.getFieldsByGroup(Course.class, DETAILS);
        if (!CollectionUtils.isEmpty(courseFields)) {
            courses = commonMongoRepository
                    .getEntitiesByIdAndFields(INSTITUTE_ID, instId, Course.class,
                            courseFields);
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
        Map<String, String> facMap = facilityMap.entrySet().stream().collect(Collectors.toMap(en ->
            en.getValue(),
            en -> facilities.contains(en.getKey().toUpperCase()) ? YES : HIPHEN));
        if (!CollectionUtils.isEmpty(facMap)) {
            return facMap;
        }
        return null;
    }

    private Map<String, Ranking> getResponseRankingMap(
            List<com.paytm.digital.education.explore.database.entity.Ranking> dbRankingList) {
        Map<String, List<Ranking>> rankingMap = new HashMap<>();
        Map<String, Ranking> retMap = new HashMap<>();

        rankingMap = dbRankingList.stream().filter(r -> {
            return (Objects.nonNull(r.getSource()) && Objects.nonNull(r.getRankingType()) && (
                    r.getRankingType().equalsIgnoreCase(UNIVERSITIES) || r.getRankingType()
                            .equalsIgnoreCase(OVERALL_RANKING)));
        }).map(r -> getRankingDTO(r)).collect(Collectors.groupingBy(r -> r.getSource(),
                Collectors.mapping(r -> r, Collectors.toList()))
        );

        for (Map.Entry<String, List<Ranking>> en : rankingMap.entrySet()) {
            String key = en.getKey();
            List<Ranking> rankingList = en.getValue();
            Ranking ranking = null;
            int latest = LATEST_YEAR;
            for (Ranking r : rankingList) {
                if (Objects.nonNull(r.getYear())) {
                    if (r.getYear() > latest) {
                        latest = r.getYear();
                        ranking = r;
                    }
                }
            }
            if (Objects.nonNull(ranking)) {
                retMap.put(ranking.getSource(), ranking);
            }
        }

        if (!CollectionUtils.isEmpty(retMap)) {
            return retMap;
        }

        return null;
    }

    private Ranking getRankingDTO(
            com.paytm.digital.education.explore.database.entity.Ranking dbRanking) {
        Ranking r = new Ranking();
        r.setRank(dbRanking.getRank());
        r.setSource(dbRanking.getSource());
        r.setYear(dbRanking.getYear());
        r.setRating(dbRanking.getRating());
        return r;
    }
}
