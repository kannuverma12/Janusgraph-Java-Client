package com.paytm.digital.education.explore.response.builders;

import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_PREFIX;
import static com.paytm.digital.education.explore.constants.ExploreConstants.APPROVALS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OVERALL_RANKING;
import static com.paytm.digital.education.explore.enums.EducationEntity.INSTITUTE;

import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.Institute;
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
import com.paytm.digital.education.explore.service.helper.WidgetsDataHelper;
import com.paytm.digital.education.explore.service.helper.StreamDataHelper;
import com.paytm.digital.education.explore.utility.CommonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Arrays;
import java.util.Objects;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class InstituteDetailResponseBuilder {

    private ExamInstanceHelper      examInstanceHelper;
    private DerivedAttributesHelper derivedAttributesHelper;
    private PlacementDataHelper     placementDataHelper;
    private CourseDetailHelper      courseDetailHelper;
    private GalleryDataHelper       galleryDataHelper;
    private FacilityDataHelper      facilityDataHelper;
    private DetailPageSectionHelper detailPageSectionHelper;
    private BannerDataHelper        bannerDataHelper;
    private WidgetsDataHelper       widgetsDataHelper;
    private StreamDataHelper        streamDataHelper;

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
        highlights.put(APPROVALS,
                CommonUtil.getApprovals(institute.getApprovals(), parentInstitutionName));
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
        instituteDetail.setWidgets(widgetsDataHelper.getWidgets(entityName,
                institute.getInstituteId()));
        if ((!CollectionUtils.isEmpty(institute.getNotableAlumni()))) {
            instituteDetail.setNotableAlumni(institute.getNotableAlumni());
        }
        if ((!CollectionUtils.isEmpty(institute.getRankings()))) {
            instituteDetail.setRankings(getRankingDetails(institute.getRankings()));
        }
        instituteDetail.setDegreeOffered(getDegreeMap(courses));
        return instituteDetail;
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
                        rDto.setLabel(Objects.nonNull(rType) ? rType : "");
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
                    r.setLabel(Objects.nonNull(streamMap.get(key)) ? streamMap.get(key) : r.getLabel());
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
            Map<String, Set<String>> degreeMap =
                    courses.stream().filter(c -> Objects.nonNull(c.getCourseLevel()))
                            .collect(Collectors
                                    .toMap(course -> course.getCourseLevel().toString(),
                                        course -> new HashSet<>(course.getMasterDegree()),
                                        (set1, set2) -> Stream.of(set1, set2)
                                                .flatMap(Set::stream)
                                                .collect(Collectors.toSet())));
            return degreeMap;
        }
        return null;
    }

    private Map<String, List<Ranking>> getRankingDetails(
            List<com.paytm.digital.education.explore.database.entity.Ranking> rankingList) {
        if (!CollectionUtils.isEmpty(rankingList)) {
            Map<String, List<Ranking>> ratingMap = rankingList.stream()
                    .filter(r -> Objects.nonNull(r.getStream()))
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