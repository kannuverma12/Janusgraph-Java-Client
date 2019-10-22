package com.paytm.digital.education.explore.response.builders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.digital.education.database.entity.Alumni;
import com.paytm.digital.education.database.entity.CampusAmbassador;
import com.paytm.digital.education.database.entity.CampusEngagement;
import com.paytm.digital.education.database.entity.Course;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.InstiPaytmKeys;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.dto.OfficialAddress;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.PublishStatus;
import com.paytm.digital.education.explore.response.dto.common.BannerData;
import com.paytm.digital.education.explore.response.dto.detail.InstituteDetail;
import com.paytm.digital.education.explore.response.dto.detail.Ranking;
import com.paytm.digital.education.explore.service.helper.BannerDataHelper;
import com.paytm.digital.education.explore.service.helper.CampusEngagementHelper;
import com.paytm.digital.education.explore.service.helper.CourseDetailHelper;
import com.paytm.digital.education.explore.service.helper.DerivedAttributesHelper;
import com.paytm.digital.education.explore.service.helper.DetailPageSectionHelper;
import com.paytm.digital.education.explore.service.helper.FacilityDataHelper;
import com.paytm.digital.education.explore.service.helper.GalleryDataHelper;
import com.paytm.digital.education.explore.service.helper.PlacementDataHelper;
import com.paytm.digital.education.explore.service.helper.StreamDataHelper;
import com.paytm.digital.education.explore.service.impl.SimilarInstituteServiceImpl;
import com.paytm.digital.education.serviceimpl.helper.ExamInstanceHelper;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.paytm.digital.education.constant.ExploreConstants.APPROVALS;
import static com.paytm.digital.education.constant.ExploreConstants.CAREER_LOGO;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_PREFIX;
import static com.paytm.digital.education.constant.ExploreConstants.NIRF_LOGO;
import static com.paytm.digital.education.constant.ExploreConstants.NOTABLE_ALUMNI_PLACEHOLDER;
import static com.paytm.digital.education.constant.ExploreConstants.OVERALL_RANKING;
import static com.paytm.digital.education.constant.ExploreConstants.RANKING_CAREER;
import static com.paytm.digital.education.constant.ExploreConstants.RANKING_LOGO;
import static com.paytm.digital.education.constant.ExploreConstants.RANKING_NIRF;
import static com.paytm.digital.education.enums.EducationEntity.INSTITUTE;

@Service
@AllArgsConstructor
public class InstituteDetailResponseBuilder {

    private static final Logger log = LoggerFactory.getLogger(InstituteDetailResponseBuilder.class);

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
    private CampusEngagementHelper      campusEngagementHelper;

    public InstituteDetail buildResponse(Institute institute, List<Course> courses,
            List<Exam> examList, Map<String, Object> examRelatedData, Set<Long> examIds,
            String parentInstitutionName, Client client, boolean derivedAttributes,
            boolean cutOffs, boolean facilities, boolean gallery, boolean placements,
            boolean notableAlumni, boolean sections, boolean widgets, boolean coursesPerDegree,
            boolean campusEngagementFlag)
            throws IOException, TimeoutException {
        InstituteDetail instituteDetail = new InstituteDetail();
        instituteDetail.setInstituteId(institute.getInstituteId());
        if (institute.getEntityType() != null) {
            instituteDetail.setInstituteType(institute.getEntityType().name());
        }
        if (institute.getGallery() != null && StringUtils
                .isNotBlank(institute.getGallery().getLogo())) {
            instituteDetail
                    .setLogoUrl(
                            CommonUtil.getLogoLink(institute.getGallery().getLogo(), INSTITUTE));
        }
        instituteDetail.setEstablishedYear(institute.getEstablishedYear());
        instituteDetail.setOfficialName(institute.getOfficialName());
        instituteDetail.setCommonName(institute.getCommonName());
        instituteDetail.setUrlDisplayName(
                CommonUtil.convertNameToUrlDisplayName(institute.getOfficialName()));
        instituteDetail.setBrochureUrl(institute.getOfficialUrlBrochure());
        if (facilities) {
            instituteDetail
                    .setFacilities(
                            facilityDataHelper.getFacilitiesData(institute.getInstituteId(),
                                    institute.getFacilities()));
        }
        if (gallery) {
            instituteDetail.setGallery(galleryDataHelper
                    .getGalleryData(institute.getInstituteId(), institute.getGallery()));
        }
        if (coursesPerDegree) {
            if (Client.APP.equals(client)) {
                Pair<Long, Map<String, List<com.paytm.digital.education.explore.response.dto.detail.Course>>>
                        courseDetailPerLevel = courseDetailHelper
                        .getCourseDataPerLevel(Arrays.asList((Object) institute.getInstituteId()),
                                institute.getEntityType(), client);
                instituteDetail.setTotalCourses(courseDetailPerLevel.getKey());
                instituteDetail.setCoursesPerLevel(courseDetailPerLevel.getValue());
            } else {
                Pair<Long, List<com.paytm.digital.education.explore.response.dto.detail.Course>>
                        courseDetail =
                        courseDetailHelper
                                .getCourseDataList(
                                        Arrays.asList((Object) institute.getInstituteId()),
                                        institute.getEntityType());
                instituteDetail.setTotalCourses(courseDetail.getKey());
                instituteDetail.setCourses(courseDetail.getValue());
            }
        }

        if (cutOffs) {
            instituteDetail
                    .setCutOffs(examInstanceHelper.getExamCutOffs(examList, examRelatedData, examIds));
        }
        String entityName = INSTITUTE.name().toLowerCase();
        Map<String, Object> highlights = new HashMap<>();
        highlights.put(entityName, institute);
        Map<String, String> approvalsMap =
                CommonUtil.getApprovals(institute.getApprovals(), parentInstitutionName);
        if (!CollectionUtils.isEmpty(approvalsMap)) {
            highlights.put(APPROVALS, approvalsMap);
        }
        if (derivedAttributes) {
            instituteDetail.setDerivedAttributes(
                    derivedAttributesHelper.getDerivedAttributes(highlights, entityName, client));
        }
        OfficialAddress officialAddress =
                CommonUtil.getOfficialAddress(institute.getInstitutionState(),
                        institute.getInstitutionCity(), institute.getPhone(), institute.getUrl(),
                        institute.getOfficialAddress());
        instituteDetail.setOfficialAddress(officialAddress);
        if (placements) {
            instituteDetail.setPlacements(placementDataHelper.getSalariesPlacements(institute));
        }
        if (sections) {
            instituteDetail.setSections(detailPageSectionHelper.getSectionOrder(entityName, client));
        }
        List<BannerData> banners =
                bannerDataHelper.getBannerData(entityName, client);
        if (Client.APP.equals(client)) {
            try {
                banners = new ObjectMapper()
                        .convertValue(banners, new TypeReference<List<BannerData>>() {
                        });
                instituteDetail.setBanner1(banners.get(0));
                instituteDetail.setBanner2(banners.get(1));
            } catch (NullPointerException | IndexOutOfBoundsException e) {
                log.error("Update banners for app in DB: {}", e.getLocalizedMessage());
            }
        } else {
            instituteDetail.setBanners(bannerDataHelper.getBannerData(entityName, client));
        }
        if (institute.getIsClient() == 1) {
            instituteDetail.setClient(true);
        }
        if (widgets) {
            instituteDetail.setWidgets(similarInstituteService.getSimilarInstitutes(institute));
        }
        if ((!CollectionUtils.isEmpty(institute.getNotableAlumni())) && notableAlumni) {
            instituteDetail.setNotableAlumni(getNotableAlumni(institute.getNotableAlumni()));
        }
        if ((!CollectionUtils.isEmpty(institute.getRankings()))) {
            instituteDetail.setRankings(getRankingDetails(institute.getRankings()));
        }
        instituteDetail.setDegreeOffered(getDegreeMap(courses));
        if (campusEngagementFlag) {
            CampusEngagement campusEngagement =
                    campusEngagementHelper.findCampusEngagementData(institute.getInstituteId());
            if (Objects.nonNull(campusEngagement)) {
                Map<String, CampusAmbassador> campusAmbassadorMap =
                        campusEngagement.getCampusAmbassadors();
                if (Objects.nonNull(campusAmbassadorMap)) {
                    instituteDetail.setCampusAmbassadors(campusEngagementHelper
                            .getCampusAmbassadorData(campusAmbassadorMap));
                }
                if (Objects.nonNull(campusEngagement.getArticles())) {
                    instituteDetail.setArticles(campusEngagementHelper
                            .getCampusArticleData(campusEngagement.getArticles(), campusAmbassadorMap));
                }
                if (Objects.nonNull(campusEngagement.getEvents())) {
                    instituteDetail.setEvents(campusEngagementHelper
                            .getCampusEventsData(campusEngagement.getEvents()));
                }
            }
        }

        if (Objects.nonNull(institute.getPaytmKeys())) {
            InstiPaytmKeys instiPaytmKeys = institute.getPaytmKeys();
            instituteDetail.setPid(instiPaytmKeys.getPid());
            instituteDetail.setMid(instiPaytmKeys.getMid());
        }
        return instituteDetail;
    }

    private List<com.paytm.digital.education.explore.response.dto.detail.Alumni> getNotableAlumni(
            List<Alumni> notableAlumni) {
        List<com.paytm.digital.education.explore.response.dto.detail.Alumni> alumniList =
                new ArrayList<>();
        List<Alumni> alumnWithoutImageList = new ArrayList<>();
        for (Alumni alumni : notableAlumni) {
            String alumniPhoto = alumni.getAlumniPhoto();
            if (Objects.nonNull(alumniPhoto)) {
                alumniPhoto = CommonUtil.getLogoLink(alumniPhoto, INSTITUTE);
                alumni.setAlumniPhoto(alumniPhoto);
                com.paytm.digital.education.explore.response.dto.detail.Alumni responseAlumni =
                        new com.paytm.digital.education.explore.response.dto.detail.Alumni();
                BeanUtils.copyProperties(alumni, responseAlumni);
                alumniList.add(responseAlumni);
            } else {
                alumnWithoutImageList.add(alumni);
            }
        }
        for (Alumni alumni : alumnWithoutImageList) {
            alumni.setAlumniPhoto(CommonUtil.getLogoLink(NOTABLE_ALUMNI_PLACEHOLDER, INSTITUTE));
            com.paytm.digital.education.explore.response.dto.detail.Alumni responseAlumni =
                    new com.paytm.digital.education.explore.response.dto.detail.Alumni();
            BeanUtils.copyProperties(alumni, responseAlumni);
            alumniList.add(responseAlumni);
        }
        return alumniList;
    }

    //can delete this in later sprint
    private List<Ranking> getRankingList(
            List<com.paytm.digital.education.database.entity.Ranking> rankingList) {
        Map<Integer, List<Ranking>> rMap = new TreeMap<>();
        if (!CollectionUtils.isEmpty(rankingList)) {
            for (com.paytm.digital.education.database.entity.Ranking r : rankingList) {
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
            com.paytm.digital.education.database.entity.Ranking dbRanking) {
        Ranking r = new Ranking();
        r.setRank(dbRanking.getRank());
        r.setSource(dbRanking.getSource());
        r.setYear(dbRanking.getYear());
        r.setRating(dbRanking.getRating());
        String rankingLogo = "";
        if (RANKING_NIRF.equals(dbRanking.getSource())) {
            rankingLogo = NIRF_LOGO;
        } else if (RANKING_CAREER.equals(dbRanking.getSource())) {
            rankingLogo = CAREER_LOGO;
        }
        if (StringUtils.isNotBlank(rankingLogo)) {
            r.setLogo(CommonUtil.getAbsoluteUrl(rankingLogo, RANKING_LOGO));
        }

        Map<String, String> streamMap = streamDataHelper.getStreamLabelMap();
        String key = null;
        if (StringUtils.isNotBlank(dbRanking.getStream())) {
            key = dbRanking.getStream().toLowerCase();
        } else if (StringUtils.isNotBlank(dbRanking.getRankingStream())) {
            key = dbRanking.getRankingStream().toLowerCase();
        }
        if (StringUtils.isNotBlank(key) && Objects.nonNull(streamMap.get(key))) {
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
                if (course.getPublishingStatus() == PublishStatus.PUBLISHED) {
                    for (String degree : course.getMasterDegree()) {
                        degreeMap.get(course.getCourseLevel().getDisplayName()).add(degree);
                    }
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
            List<com.paytm.digital.education.database.entity.Ranking> rankingList) {
        if (!CollectionUtils.isEmpty(rankingList)) {
            Map<String, List<Ranking>> ratingMap = rankingList.stream()
                    .filter(r -> (Objects.nonNull(r.getStream()) || Objects
                            .nonNull(r.getRankingStream())) && (Objects.nonNull(r.getRank())
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
