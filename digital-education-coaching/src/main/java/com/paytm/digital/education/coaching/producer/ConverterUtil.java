package com.paytm.digital.education.coaching.producer;

import com.paytm.digital.education.coaching.producer.model.request.CoachingCenterDataRequest;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseDataRequest;
import com.paytm.digital.education.coaching.producer.model.request.CoachingExamDataRequest;
import com.paytm.digital.education.coaching.producer.model.request.CoachingInstituteDataRequest;
import com.paytm.digital.education.coaching.producer.model.request.StreamDataRequest;
import com.paytm.digital.education.coaching.producer.model.request.TopRankerDataRequest;
import com.paytm.digital.education.database.embedded.KeyHighlight;
import com.paytm.digital.education.database.embedded.OfficialAddress;
import com.paytm.digital.education.database.entity.CoachingCenterEntity;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.database.entity.CoachingExamEntity;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.entity.TopRankerEntity;

import java.util.stream.Collectors;

public class ConverterUtil {

    public static void setStreamData(StreamDataRequest request, StreamEntity streamEntity) {
        streamEntity.setLogo(request.getLogo());
        streamEntity.setName(request.getName());
        streamEntity.setTopInstitutes(request.getTopInstitutes());
        streamEntity.setPriority(request.getPriority());
        streamEntity.setIsEnabled(request.getIsEnabled());
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
    }

    public static void setCoachingExam(CoachingExamDataRequest request,
            CoachingExamEntity coachingExamEntity) {

        coachingExamEntity.setInstituteId(request.getInstituteId());
        coachingExamEntity.setExamType(request.getExamType());
        coachingExamEntity.setExamName(request.getExamName());
        coachingExamEntity.setExamDescription(request.getExamDescription());
        coachingExamEntity.setCourseId(request.getProgramId());
        coachingExamEntity.setStreamId(request.getStreamId());
        coachingExamEntity.setExamDuration(request.getExamDuration());
        coachingExamEntity.setMaximumMarks(request.getMaximumMarks());
        coachingExamEntity.setExamDate(request.getExamDate());
        coachingExamEntity.setEligibility(request.getEligibility());

        coachingExamEntity.setIsEnabled(request.getIsEnabled());
        coachingExamEntity.setPriority(request.getPriority());
    }

    public static void setTopRanker(TopRankerDataRequest request, TopRankerEntity topRankerEntity) {
        topRankerEntity.setInstituteId(request.getInstituteId());
        topRankerEntity.setCenterId(request.getCenterId());
        topRankerEntity.setBatch(request.getBatchInfo());
        topRankerEntity.setCourseIds(request.getCourseStudied());
        topRankerEntity.setExamId(request.getExamId());
        topRankerEntity.setExamYear(request.getExamYear());
        topRankerEntity.setRankObtained(request.getRankObtained());
        topRankerEntity.setStudentName(request.getStudentName());
        topRankerEntity.setStudentPhoto(request.getStudentPhoto());
        topRankerEntity.setTestimonial(request.getTestimonial());
        topRankerEntity.setYear(request.getExamYear());
        topRankerEntity.setCollegeAdmitted(request.getCollegeAdmitted());

        topRankerEntity.setIsEnabled(request.getIsEnabled());
        topRankerEntity.setPriority(request.getPriority());
    }

    public static void setCoachingCourse(CoachingCourseDataRequest request,
            CoachingCourseEntity coachingCourseEntity) {
        coachingCourseEntity.setName(request.getName());
        coachingCourseEntity.setCoachingInstituteId(request.getInstituteId());
        coachingCourseEntity.setCourseType(request.getCourseType());
        coachingCourseEntity.setStreamIds(request.getStreamIds());
        coachingCourseEntity.setPrimaryExamIds(request.getPrimaryExamIds());
        coachingCourseEntity.setDuration(request.getDuration());
        coachingCourseEntity.setDurationType(request.getDurationType());
        coachingCourseEntity.setEligibility(request.getEligibility());
        coachingCourseEntity.setInfo(request.getInfo());
        coachingCourseEntity.setDescription(request.getDescription());
        coachingCourseEntity.setFeatures(request.getCourseFeatureIds());
        coachingCourseEntity.setPrice(request.getPrice());
        coachingCourseEntity.setLevel(request.getLevel());
        coachingCourseEntity.setLanguage(request.getLanguage());
        coachingCourseEntity.setSyllabus(request.getSyllabusAndBrochure());

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

        coachingCourseEntity.setIsEnabled(request.getIsEnabled());
        coachingCourseEntity.setPriority(request.getPriority());
    }


}


