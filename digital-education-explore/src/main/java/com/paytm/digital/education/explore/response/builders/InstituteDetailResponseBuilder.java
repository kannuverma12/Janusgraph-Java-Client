package com.paytm.digital.education.explore.response.builders;

import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_PREFIX;
import static com.paytm.digital.education.explore.constants.ExploreConstants.APPROVALS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OVERALL_RANKING;
import static com.paytm.digital.education.explore.enums.EducationEntity.INSTITUTE;

import com.paytm.digital.education.explore.database.entity.Alumni;
import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.enums.CourseLevel;
import com.paytm.digital.education.explore.response.dto.common.OfficialAddress;
import com.paytm.digital.education.explore.response.dto.detail.InstituteDetail;
import com.paytm.digital.education.explore.response.dto.detail.Ranking;
import com.paytm.digital.education.explore.service.helper.ExamInstanceHelper;
import com.paytm.digital.education.explore.service.helper.DerivedAttributesHelper;
import com.paytm.digital.education.explore.service.helper.PlacementDataHelper;
import com.paytm.digital.education.explore.service.helper.CourseDetailHelper;
import com.paytm.digital.education.explore.service.helper.GalleryDataHelper;
import com.paytm.digital.education.explore.service.helper.FacilityDataHelper;
import com.paytm.digital.education.explore.service.helper.DetailPageSectionHelper;
import com.paytm.digital.education.explore.service.helper.BannerDataHelper;
import com.paytm.digital.education.explore.service.helper.StreamDataHelper;
import com.paytm.digital.education.explore.service.impl.SimilarInstituteServiceImpl;
import com.paytm.digital.education.explore.utility.CommonUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Objects;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.stream.Collectors;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
@AllArgsConstructor
public class InstituteDetailResponseBuilder {

    private ExamInstanceHelper          examInstanceHelper;
    private DerivedAttributesHelper     derivedAttributesHelper;
    private PlacementDataHelper         placementDataHelper;
    private CourseDetailHelper          courseDetailHelper;
    private GalleryDataHelper           galleryDataHelper;
    private FacilityDataHelper          facilityDataHelper;
    private DetailPageSectionHelper     detailPageSectionHelper;
    private BannerDataHelper            bannerDataHelper;
    private SimilarInstituteServiceImpl similarInstituteService;
    private StreamDataHelper            streamDataHelper;

    public InstituteDetail buildResponse(Institute institute, List<Course> courses,
            List<Exam> examList, Map<String, Object> examRelatedData, Set<Long> examIds,
            String parentInstitutionName)
            throws IOException, TimeoutException {
        InstituteDetail instituteDetail = new InstituteDetail();
        instituteDetail.setInstituteId(institute.getInstituteId());
        if (institute.getEntityType() != null) {
            instituteDetail.setInstituteType(institute.getEntityType().name());
        }
        if (institute.getGallery() != null && StringUtils
                .isNotBlank(institute.getGallery().getLogo())) {
            instituteDetail
                    .setLogoUrl(CommonUtil.getLogoLink(institute.getGallery().getLogo()));
        }
        instituteDetail.setEstablishedYear(institute.getEstablishedYear());
        instituteDetail.setOfficialName(institute.getOfficialName());
        instituteDetail.setBrochureUrl(institute.getOfficialUrlBrochure());
        instituteDetail
                .setFacilities(
                        facilityDataHelper.getFacilitiesData(institute.getInstituteId(),
                                institute.getFacilities()));
        instituteDetail.setGallery(galleryDataHelper
                .getGalleryData(institute.getInstituteId(), institute.getGallery()));
        Pair<Long, List<com.paytm.digital.education.explore.response.dto.detail.Course>>
                courseDetail =
                courseDetailHelper
                        .getCourseDataList(Arrays.asList((Object) institute.getInstituteId()),
                                institute.getEntityType());
        instituteDetail.setTotalCourses(courseDetail.getKey());
        instituteDetail.setCourses(courseDetail.getValue());
        instituteDetail
                .setCutOffs(examInstanceHelper.getExamCutOffs(examList, examRelatedData, examIds));
        String entityName = INSTITUTE.name().toLowerCase();
        Map<String, Object> highlights = new HashMap<>();
        highlights.put(entityName, institute);
        Map<String, String> approvalsMap =
                CommonUtil.getApprovals(institute.getApprovals(), parentInstitutionName);
        if (!CollectionUtils.isEmpty(approvalsMap)) {
            highlights.put(APPROVALS, approvalsMap);
        }
        instituteDetail.setDerivedAttributes(
                derivedAttributesHelper.getDerivedAttributes(highlights, entityName));
        OfficialAddress officialAddress =
                CommonUtil.getOfficialAddress(institute.getInstitutionState(),
                        institute.getInstitutionCity(), institute.getPhone(), institute.getUrl(),
                        institute.getOfficialAddress());
        instituteDetail.setOfficialAddress(officialAddress);
        instituteDetail.setPlacements(placementDataHelper.getSalariesPlacements(institute));
        instituteDetail.setSections(detailPageSectionHelper.getSectionOrder(entityName));
        instituteDetail.setBanners(bannerDataHelper.getBannerData(entityName));
        if (institute.getIsClient() == 1) {
            instituteDetail.setClient(true);
        }
        instituteDetail.setWidgets(similarInstituteService.getSimilarInstitutes(institute));
        if ((!CollectionUtils.isEmpty(institute.getNotableAlumni()))) {
            instituteDetail.setNotableAlumni(getNotableAlumni(institute.getNotableAlumni()));
        }
        if ((!CollectionUtils.isEmpty(institute.getRankings()))) {
            instituteDetail.setRankings(getRankingDetails(institute.getRankings()));
        }
        instituteDetail.setDegreeOffered(getDegreeMap(courses));
        return instituteDetail;
    }

    private List<com.paytm.digital.education.explore.response.dto.detail.Alumni> getNotableAlumni(
            List<Alumni> notableAlumni) {
        List<com.paytm.digital.education.explore.response.dto.detail.Alumni> alumniList =
                new ArrayList<>();
        for (Alumni alumni : notableAlumni) {
            String alumniPhoto = alumni.getAlumniPhoto();
            if (Objects.nonNull(alumniPhoto)) {
                alumniPhoto = CommonUtil.getLogoLink(alumniPhoto);
                alumni.setAlumniPhoto(alumniPhoto);
            }
            com.paytm.digital.education.explore.response.dto.detail.Alumni responseAlumni =
                    new com.paytm.digital.education.explore.response.dto.detail.Alumni();
            BeanUtils.copyProperties(alumni, responseAlumni);
            alumniList.add(responseAlumni);
        }
        return alumniList;
    }

    //can delete this in later sprint
    private List<Ranking> getRankingList(
            List<com.paytm.digital.education.explore.database.entity.Ranking> rankingList) {
        Map<Integer, List<Ranking>> rMap = new TreeMap<>();
        if (!CollectionUtils.isEmpty(rankingList)) {
            for (com.paytm.digital.education.explore.database.entity.Ranking r : rankingList) {
                String rStream = r.getStream();
                String rType = r.getRankingType();
                Ranking rDto = getResponseRanking(r);
                if (Objects.nonNull(rType) && rType.equalsIgnoreCase(OVERALL_RANKING)) {
                    rDto.setLabel(OVERALL_RANKING);
                    updateMap(rMap, rDto, 1);
                } else if (!StringUtils.isEmpty(rStream)) {
                    rDto.setLabel(rStream);
                    updateMap(rMap, rDto, 2);
                } else if (StringUtils.isEmpty(rStream)) {
                    if (StringUtils.isEmpty(rType)) {
                        rDto.setLabel(INSTITUTE_PREFIX);
                        updateMap(rMap, rDto, 4);
                    } else {
                        rDto.setLabel(Objects.nonNull(rType)
                                ? rType : "");
                        updateMap(rMap, rDto, 3);
                    }
                }
            }
        }

        Map<String, String> streamMap = streamDataHelper.getStreamMap();

        List<Ranking> rList = rMap.values().stream()
                .flatMap(List::stream)
                .filter(r -> Objects.nonNull(r.getLabel()))
                .map(r -> {
                    String key = r.getLabel().toLowerCase();
                    r.setLabel(Objects.nonNull(streamMap.get(key))
                            ? streamMap.get(key) : r.getLabel());
                    return r;
                })
                .collect(Collectors.toList());
        return rList;
    }

    private void updateMap(Map<Integer, List<Ranking>> map, Ranking r, int i) {
        List<Ranking> list = map.get(i);
        if (Objects.isNull(list)) {
            list = new ArrayList<>();
        }
        list.add(r);
        map.put(i, list);
    }

    private Ranking getResponseRanking(
            com.paytm.digital.education.explore.database.entity.Ranking dbRanking) {
        Ranking r = new Ranking();
        r.setRank(dbRanking.getRank());
        r.setSource(dbRanking.getSource());
        r.setYear(dbRanking.getYear());
        r.setRating(dbRanking.getRating());

        Map<String, String> streamMap = streamDataHelper.getStreamLabelMap();
        String key = dbRanking.getStream().toLowerCase();
        if (Objects.nonNull(streamMap.get(key))) {
            r.setLabel(streamMap.get(key));
        }

        if (StringUtils.isEmpty(dbRanking.getStream()) && !StringUtils
                .isEmpty(dbRanking.getRankingType())) {
            String key1 = dbRanking.getRankingType().toLowerCase();
            if (Objects.nonNull(streamMap.get(key1))) {
                r.setLabel(streamMap.get(key1));
            }
        }
        return r;
    }

    private Map<String, Set<String>> getDegreeMap(List<Course> courses) {
        if (!CollectionUtils.isEmpty(courses)) {
            Map<String, Set<String>> degreeMap = new LinkedHashMap<>();
            degreeMap.put(CourseLevel.UNDERGRADUATE.getDisplayName(), new HashSet<>());
            degreeMap.put(CourseLevel.POSTGRADUATE.getDisplayName(), new HashSet<>());
            degreeMap.put(CourseLevel.DOCTORATE.getDisplayName(), new HashSet<>());
            degreeMap.put(CourseLevel.DIPLOMA.getDisplayName(), new HashSet<>());
            for (Course course : courses) {
                for (String degree : course.getMasterDegree()) {
                    degreeMap.get(course.getCourseLevel().getDisplayName()).add(degree);
                }
            }
            List<String> emptyLevels = new ArrayList<>();
            for (Map.Entry<String, Set<String>> entry : degreeMap.entrySet()) {
                if (CollectionUtils.isEmpty(entry.getValue())) {
                    emptyLevels.add(entry.getKey());
                }
            }
            for (String level : emptyLevels) {
                degreeMap.remove(level);
            }
            if (!CollectionUtils.isEmpty(degreeMap)) {
                return degreeMap;
            }
        }
        return null;
    }

    private Map<String, List<Ranking>> getRankingDetails(
            List<com.paytm.digital.education.explore.database.entity.Ranking> rankingList) {
        if (!CollectionUtils.isEmpty(rankingList)) {
            Map<String, List<Ranking>> ratingMap = rankingList.stream()
                    .filter(r -> Objects.nonNull(r.getStream()) && (Objects.nonNull(r.getRank())
                            || Objects.nonNull(r.getRating())))
                    .map(r -> getResponseRanking(r))
                    .filter(r -> Objects.nonNull(r.getLabel()))
                    .collect(Collectors.groupingBy(r -> r.getLabel(),
                            Collectors.mapping(r -> r, Collectors.toList()))
                    );
            return ratingMap;
        }
        return null;
    }

}
