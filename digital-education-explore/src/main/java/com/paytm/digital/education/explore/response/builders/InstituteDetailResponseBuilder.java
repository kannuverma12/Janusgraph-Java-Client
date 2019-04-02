package com.paytm.digital.education.explore.response.builders;

import static com.paytm.digital.education.explore.constants.ExploreConstants.OVERALL_RANKING;
import static com.paytm.digital.education.explore.enums.EducationEntity.INSTITUTE;

import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.response.dto.common.OfficialAddress;
import com.paytm.digital.education.explore.response.dto.detail.InstituteDetail;
import com.paytm.digital.education.explore.response.dto.detail.Ranking;
import com.paytm.digital.education.explore.service.helper.BannerDataHelper;
import com.paytm.digital.education.explore.service.helper.CourseDetailHelper;
import com.paytm.digital.education.explore.service.helper.DerivedAttributesHelper;
import com.paytm.digital.education.explore.service.helper.DetailPageSectionHelper;
import com.paytm.digital.education.explore.service.helper.ExamInstanceHelper;
import com.paytm.digital.education.explore.service.helper.FacilityDataHelper;
import com.paytm.digital.education.explore.service.helper.GalleryDataHelper;
import com.paytm.digital.education.explore.service.helper.PlacementDataHelper;
import com.paytm.digital.education.explore.service.helper.WidgetsDataHelper;
import com.paytm.digital.education.explore.utility.CommonUtil;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

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

    public InstituteDetail buildResponse(Institute institute, List<Course> courses,
            List<Exam> examList) throws IOException, TimeoutException {
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
                        facilityDataHelper.getFacilitiesData(institute.getInstituteId(), institute.getFacilities()));
        instituteDetail.setGallery(galleryDataHelper
                .getGalleryData(institute.getInstituteId(), institute.getGallery()));
        Pair<Long, List<com.paytm.digital.education.explore.response.dto.detail.Course>> courseDetail =
                courseDetailHelper.getCourseDataList(Arrays.asList((Object) institute.getInstituteId()),
                        institute.getEntityType());
        instituteDetail.setTotalCourses(courseDetail.getKey());
        instituteDetail.setCourses(courseDetail.getValue());
        instituteDetail.setCutOff(examInstanceHelper.getExamCutOffs(examList));
        String entityName = INSTITUTE.name().toLowerCase();
        Map<String, Object> highlights = new HashMap<>();
        highlights.put(entityName, institute);
        instituteDetail.setDerivedAttributes(
                derivedAttributesHelper.getDerivedAttributes(highlights, entityName));
        OfficialAddress officialAddress = CommonUtil.getOfficialAddress(institute.getInstitutionState(),
                institute.getInstitutionCity(), institute.getPhone(), institute.getUrl(),
                institute.getOfficialAddress());
        instituteDetail.setOfficialAddress(officialAddress);
        instituteDetail.setRankings(getRanking(institute.getRankings()));
        instituteDetail.setPlacements(placementDataHelper.getSalariesPlacements(institute));
        instituteDetail.setSections(detailPageSectionHelper.getSectionOrder(entityName));
        instituteDetail.setBanners(bannerDataHelper.getBannerData(entityName));
        instituteDetail.setWidgets(widgetsDataHelper.getWidgets(entityName));
        return instituteDetail;
    }

    private List<Ranking> getRanking(
            List<com.paytm.digital.education.explore.database.entity.Ranking> rankingList) {
        if (!CollectionUtils.isEmpty(rankingList)) {
            List<Ranking> rankings = new ArrayList<>();
            rankingList.forEach(ranking -> {
                if (!OVERALL_RANKING.equalsIgnoreCase(ranking.getCategory())) {
                    Ranking rankingData = new Ranking();
                    BeanUtils.copyProperties(ranking, rankingData);
                    rankings.add(rankingData);
                }
            });
            return rankings;
        }
        return null;
    }
}
