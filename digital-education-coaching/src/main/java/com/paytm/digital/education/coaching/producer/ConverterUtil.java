package com.paytm.digital.education.coaching.producer;

import com.paytm.digital.education.coaching.enums.CtaType;
import com.paytm.digital.education.coaching.producer.model.dto.CoachingCtaDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingBannerDataRequest;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCenterDataRequest;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseDataRequest;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseFeatureDataRequest;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCoursePatchRequest;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCtaDataRequest;
import com.paytm.digital.education.coaching.producer.model.request.CoachingExamDataRequest;
import com.paytm.digital.education.coaching.producer.model.request.CoachingInstituteDataRequest;
import com.paytm.digital.education.coaching.producer.model.request.StreamDataRequest;
import com.paytm.digital.education.coaching.producer.model.request.TargetExamUpdateRequest;
import com.paytm.digital.education.coaching.producer.model.request.TopRankerDataRequest;
import com.paytm.digital.education.database.embedded.CoachingCourseImportantDate;
import com.paytm.digital.education.database.embedded.Faq;
import com.paytm.digital.education.database.embedded.KeyHighlight;
import com.paytm.digital.education.database.embedded.OfficialAddress;
import com.paytm.digital.education.database.entity.CoachingBannerEntity;
import com.paytm.digital.education.database.entity.CoachingCenterEntity;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.database.entity.CoachingCourseFeatureEntity;
import com.paytm.digital.education.database.entity.CoachingCtaEntity;
import com.paytm.digital.education.database.entity.CoachingExamEntity;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.entity.TopRankerEntity;

import java.util.stream.Collectors;

public class ConverterUtil {

    public static void setStreamData(StreamDataRequest request, StreamEntity streamEntity) {
        streamEntity.setLogo(request.getLogo());
        streamEntity.setName(request.getName());
        streamEntity.setPriority(request.getPriority());
        streamEntity.setIsEnabled(request.getIsEnabled());
        streamEntity.setShortName(request.getShortName());
    }

    public static void setCoachingInstituteData(CoachingInstituteDataRequest request,
            CoachingInstituteEntity coachingInstituteEntity) {
        coachingInstituteEntity.setAboutInstitute(request.getAboutInstitute());
        coachingInstituteEntity.setBrandName(request.getBrandName());
        coachingInstituteEntity.setOfficialAddress(OfficialAddress.builder()
                .addressLine1(request.getOfficialAddress().getAddressLine1())
                .addressLine2(request.getOfficialAddress().getAddressLine2())
                .addressLine3(request.getOfficialAddress().getAddressLine3())
                .city(request.getOfficialAddress().getCity())
                .state(request.getOfficialAddress().getState())
                .pincode(request.getOfficialAddress().getPincode())
                .latitude(request.getOfficialAddress().getLatitude())
                .longitude(request.getOfficialAddress().getLongitude())
                .email(request.getOfficialAddress().getEmail())
                .phone(request.getOfficialAddress().getPhone())
                .build());
        coachingInstituteEntity.setLogo(request.getLogo());
        coachingInstituteEntity.setCoverImage(request.getCoverImage());
        coachingInstituteEntity.setStreams(request.getStreamIds());
        coachingInstituteEntity.setExams(request.getExamIds());
        coachingInstituteEntity.setCourseTypes(request.getCourseTypes());
        coachingInstituteEntity.setEstablishmentYear(request.getEstablishmentYear());
        coachingInstituteEntity.setBrochure(request.getBrochureUrl());
        coachingInstituteEntity.setKeyHighlights(request.getHighlights().stream().map(requestData ->
                KeyHighlight.builder().key(requestData.getKey())
                        .logo(requestData.getLogo())
                        .value(requestData.getValue()).build()
        ).collect(Collectors.toList()));

        coachingInstituteEntity.setCourseLevels(request.getCourseLevels());

        coachingInstituteEntity.setPaytmMerchantId(request.getPaytmMerchantId());
        coachingInstituteEntity.setMoreInfo1(request.getMoreInfo1());
        coachingInstituteEntity.setMoreInfo2(request.getMoreInfo2());
        coachingInstituteEntity.setMoreInfo3(request.getMoreInfo3());
        coachingInstituteEntity.setMoreInfo4(request.getMoreInfo4());
        coachingInstituteEntity.setFaqs(request.getFaqs().stream()
                .map(faq -> Faq.builder()
                        .question(faq.getQuestion())
                        .answers(faq.getAnswers())
                        .build())
                .collect(Collectors.toList()));

        coachingInstituteEntity.setIsEnabled(request.getIsEnabled());
        coachingInstituteEntity.setPriority(request.getPriority());
    }

    public static void setCoachingCenter(CoachingCenterDataRequest request,
            CoachingCenterEntity coachingCenterEntity) {
        coachingCenterEntity.setCourseTypes(request.getCourseTypes());
        coachingCenterEntity.setOfficialAddress(OfficialAddress.builder()
                .addressLine1(request.getOfficialAddress().getAddressLine1())
                .addressLine2(request.getOfficialAddress().getAddressLine2())
                .addressLine3(request.getOfficialAddress().getAddressLine3())
                .city(request.getOfficialAddress().getCity())
                .state(request.getOfficialAddress().getState())
                .pincode(request.getOfficialAddress().getPincode())
                .latitude(request.getOfficialAddress().getLatitude())
                .longitude(request.getOfficialAddress().getLongitude())
                .email(request.getOfficialAddress().getEmail())
                .phone(request.getOfficialAddress().getPhone())
                .build());
        coachingCenterEntity.setOfficialName(request.getOfficialName());
        coachingCenterEntity.setInstituteId(request.getInstituteId());
        coachingCenterEntity.setIsEnabled(request.getIsEnabled());
        coachingCenterEntity.setPriority(request.getPriority());
        coachingCenterEntity.setOpeningTime(request.getOpeningTime());
        coachingCenterEntity.setClosingTime(request.getClosingTime());
        coachingCenterEntity.setCenterImage(request.getCenterImage());
    }

    public static void setCoachingExam(CoachingExamDataRequest request,
            CoachingExamEntity coachingExamEntity) {

        coachingExamEntity.setInstituteId(request.getInstituteId());
        coachingExamEntity.setExamType(request.getExamType());
        coachingExamEntity.setExamName(request.getExamName());
        coachingExamEntity.setExamDescription(request.getExamDescription());
        coachingExamEntity.setCourseIds(request.getCourseIds());
        coachingExamEntity.setStreamIds(request.getStreamIds());
        coachingExamEntity.setExamDuration(request.getExamDuration());
        coachingExamEntity.setMaximumMarks(request.getMaximumMarks());
        coachingExamEntity.setExamDate(request.getExamDate());
        coachingExamEntity.setEligibility(request.getEligibility());
        coachingExamEntity.setQuestionCount(request.getQuestionCount());

        coachingExamEntity.setIsEnabled(request.getIsEnabled());
        coachingExamEntity.setPriority(request.getPriority());
    }

    public static void setTopRanker(TopRankerDataRequest request, TopRankerEntity topRankerEntity) {
        topRankerEntity.setInstituteId(request.getInstituteId());
        topRankerEntity.setCenterId(request.getCenterId());
        topRankerEntity.setBatch(request.getBatchInfo());
        topRankerEntity.setCourseIds(request.getCourseStudied());
        topRankerEntity.setExamId(request.getExamId());
        topRankerEntity.setStreamIds(request.getStreamIds());
        topRankerEntity.setExamYear(request.getExamYear());
        topRankerEntity.setRankObtained(request.getRankObtained());
        topRankerEntity.setStudentName(request.getStudentName());
        topRankerEntity.setStudentPhoto(request.getStudentPhoto());
        topRankerEntity.setTestimonial(request.getTestimonial());
        topRankerEntity.setStudentCategory(request.getStudentCategory());
        topRankerEntity.setCollegeAdmitted(request.getCollegeAdmitted());

        topRankerEntity.setIsEnabled(request.getIsEnabled());
        topRankerEntity.setPriority(request.getPriority());
    }

    public static void setCoachingCourse(CoachingCourseDataRequest request,
            CoachingCourseEntity coachingCourseEntity) {
        coachingCourseEntity.setName(request.getName());
        coachingCourseEntity.setCoachingInstituteId(request.getInstituteId());
        coachingCourseEntity.setCourseType(request.getCourseType());
        coachingCourseEntity.setCourseCover(request.getCourseCover());
        coachingCourseEntity.setStreamIds(request.getStreamIds());
        coachingCourseEntity.setPrimaryExamIds(request.getPrimaryExamIds());
        coachingCourseEntity.setDuration(request.getDuration());
        coachingCourseEntity.setDurationType(request.getDurationType());
        coachingCourseEntity.setValidity(request.getValidity());
        coachingCourseEntity.setValidityType(request.getValidityType());
        coachingCourseEntity.setEligibility(request.getEligibility());
        coachingCourseEntity.setInfo(request.getInfo());
        coachingCourseEntity.setDescription(request.getDescription());
        coachingCourseEntity.setOriginalPrice(request.getOriginalPrice());
        coachingCourseEntity.setDiscountedPrice(request.getDiscountedPrice());
        coachingCourseEntity.setCourseLevel(request.getCourseLevel());
        coachingCourseEntity.setLanguage(request.getLanguage());
        coachingCourseEntity.setSyllabus(request.getSyllabusAndBrochure());
        coachingCourseEntity.setImportantDates(request.getImportantDates().stream()
                .map(input -> CoachingCourseImportantDate.builder()
                        .key(input.getKey())
                        .value(input.getValue()).build()).collect(Collectors.toList()));

        // common across all the courses
        coachingCourseEntity
                .setIsCertificateAvailable(request.getIsCertificateAvailable());
        coachingCourseEntity.setIsDoubtSolvingForumAvailable(
                request.getIsDoubtSolvingForumAvailable());
        coachingCourseEntity.setIsProgressAnalysisAvailable(
                request.getIsProgressAnalysisAvailable());
        coachingCourseEntity.setIsRankAnalysisAvailable(
                request.getIsRankAnalysisAvailable());

        // test course
        coachingCourseEntity.setTestCount(request.getTestCount());
        coachingCourseEntity.setTestDuration(request.getTestDuration());
        coachingCourseEntity
                .setTestPracticePaperCount(request.getTestPracticePaperCount());

        // distance course
        coachingCourseEntity.setDistanceLearningAssignmentsCount(
                request.getDistanceLearningAssignmentsCount());

        coachingCourseEntity.setDistanceLearningBooksCount(
                request.getDistanceLearningBooksCount());
        coachingCourseEntity.setDistanceLearningSolvedPaperCount(
                request.getDistanceLearningSolvedPaperCount());

        // elearning course
        coachingCourseEntity
                .setElearningLectureCount(request.getElearningLectureCount());
        coachingCourseEntity.setElearningLectureDuration(
                request.getElearningLectureDuration());
        coachingCourseEntity.setElearningOnlineTestCount(
                request.getElearningOnlineTestCount());
        coachingCourseEntity.setElearningPracticePaperCount(
                request.getElearningPracticePaperCount());

        // classroom courses
        coachingCourseEntity
                .setClassroomLectureCount(request.getClassroomLectureCount());
        coachingCourseEntity.setClassroomLectureDuration(
                request.getClassroomLectureDuration());
        coachingCourseEntity.setClassroomTestCount(request.getClassroomTestCount());
        coachingCourseEntity.setClassroomTeacherStudentRatio(
                request.getClassroomTeacherStudentRatio());

        // extra fields
        coachingCourseEntity.setHowToUse1(request.getHowToUse1());
        coachingCourseEntity.setHowToUse2(request.getHowToUse2());
        coachingCourseEntity.setHowToUse3(request.getHowToUse3());
        coachingCourseEntity.setHowToUse4(request.getHowToUse4());

        coachingCourseEntity.setMerchantProductId(request.getMerchantProductId());
        coachingCourseEntity.setPaytmProductId(request.getPaytmProductId());

        coachingCourseEntity.setSgst(request.getSgst());
        coachingCourseEntity.setCgst(request.getCgst());
        coachingCourseEntity.setIgst(request.getIgst());
        coachingCourseEntity.setTcs(request.getTcs());

        coachingCourseEntity.setCourseFeatureIds(request.getCourseFeatureIds());

        coachingCourseEntity.setIsEnabled(request.getIsEnabled());
        coachingCourseEntity.setPriority(request.getPriority());

        coachingCourseEntity.setIsDynamic(request.getIsDynamic());
    }

    public static void patchCoachingCourse(CoachingCoursePatchRequest request,
            CoachingCourseEntity coachingCourseEntity) {
        coachingCourseEntity.setCtaInfo(request.getCtaInfo());
    }

    public static void setExamUpdateData(TargetExamUpdateRequest request,
            Exam exam) {
        exam.setStreamIds(request.getStreamIds());
        exam.setPriority(request.getPriority());
    }

    public static void setCoachingBannerData(CoachingBannerDataRequest request, CoachingBannerEntity
            coachingBannerEntity) {
        coachingBannerEntity.setCoachingBannerId(request.getCoachingBannerId());
        coachingBannerEntity.setBannerImageUrl(request.getBannerImageUrl());
        coachingBannerEntity.setPriority(request.getPriority());
        coachingBannerEntity.setIsEnabled(request.getIsEnabled());
        coachingBannerEntity.setRedirectionUrl(request.getRedirectionUrl());
    }

    public static void setCoachingCourseFeatureData(CoachingCourseFeatureDataRequest request,
            CoachingCourseFeatureEntity coachingCourseFeatureEntity) {
        coachingCourseFeatureEntity
                .setCoachingCourseFeatureId(request.getCoachingCourseFeatureId());
        coachingCourseFeatureEntity.setDescription(request.getDescription());
        coachingCourseFeatureEntity.setInstituteId(request.getInstituteId());
        coachingCourseFeatureEntity.setLogo(request.getLogo());
        coachingCourseFeatureEntity.setName(request.getCoachingCourseFeatureName().getText());
        coachingCourseFeatureEntity.setPriority(request.getPriority());
        coachingCourseFeatureEntity.setIsEnabled(request.getIsEnabled());
    }

    public static CoachingCtaEntity toCtaEntity(CoachingCtaDataRequest ctaRequest,
            CoachingCtaEntity coachingCtaEntity) {

        return coachingCtaEntity
                .setCtaId(ctaRequest.getCtaId())
                .setCtaType(ctaRequest.getCtaType().name())
                .setDescription(ctaRequest.getDescription())
                .setLogoUrl(ctaRequest.getLogoUrl())
                .setName(ctaRequest.getName())
                .setProperties(ctaRequest.getProperties())
                .setUrl(ctaRequest.getUrl());
    }

    public static CoachingCtaDTO toCtaDTO(CoachingCtaEntity ctaEntity) {

        return new CoachingCtaDTO()
                .setCtaId(ctaEntity.getCtaId())
                .setCtaType(CtaType.valueOf(ctaEntity.getCtaType()))
                .setDescription(ctaEntity.getDescription())
                .setLogoUrl(ctaEntity.getLogoUrl())
                .setName(ctaEntity.getName())
                .setProperties(ctaEntity.getProperties())
                .setUrl(ctaEntity.getUrl());

    }
}


