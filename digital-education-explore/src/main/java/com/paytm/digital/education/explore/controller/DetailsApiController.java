package com.paytm.digital.education.explore.controller;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

import java.util.List;

import com.paytm.digital.education.explore.enums.Client;
import com.paytm.digital.education.explore.response.dto.detail.CourseDetail;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.SchoolDetail;
import com.paytm.digital.education.explore.service.SchoolService;
import com.paytm.digital.education.explore.service.impl.CourseDetailServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.paytm.digital.education.explore.response.dto.detail.ExamDetail;
import com.paytm.digital.education.explore.response.dto.detail.InstituteDetail;
import com.paytm.digital.education.explore.service.impl.ExamDetailServiceImpl;
import com.paytm.digital.education.explore.service.impl.InstituteDetailServiceImpl;
import com.paytm.digital.education.explore.validators.ExploreValidator;
import lombok.AllArgsConstructor;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;


@AllArgsConstructor
@RestController
@Validated
@RequestMapping(EDUCATION_BASE_URL)
public class DetailsApiController {

    private InstituteDetailServiceImpl instituteDetailService;
    private CourseDetailServiceImpl    courseDetailService;
    private ExamDetailServiceImpl      examDetailService;
    private ExploreValidator           exploreValidator;
    private SchoolService              schoolService;

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/exam/{examId}/{examName}")
    public @ResponseBody ExamDetail getExamById(
            @PathVariable("examId") @Min(1) Long examId,
            @PathVariable("examName") @NotBlank String examName,
            @RequestParam(name = "field_group", required = false) String fieldGroup,
            @RequestParam(name = "fields", required = false) List<String> fields,
            @RequestParam(value = "syllabus", required = false, defaultValue = "true")
                    Boolean syllabus,
            @RequestParam(value = "important_dates", required = false, defaultValue = "true")
                    Boolean importantDates,
            @RequestParam(value = "derived_attributes", required = false, defaultValue = "true")
                    Boolean derivedAttributes,
            @RequestParam(value = "exam-centers", required = false, defaultValue = "true")
                    Boolean examCenters,
            @RequestParam(value = "sections", required = false, defaultValue = "true")
                    Boolean sections,
            @RequestParam(value = "widgets", required = false, defaultValue = "true")
                    Boolean widgets,
            @RequestHeader(value = "x-user-id", required = false) Long userId,
            @RequestHeader(value = "fe_client", required = false) Client client) throws Exception {
        exploreValidator.validateFieldAndFieldGroup(fields, fieldGroup);

        return examDetailService
                .getDetail(examId, examName, userId, fieldGroup, fields, client, syllabus,
                        importantDates, derivedAttributes, examCenters, sections,
                        widgets);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/institute/{instituteId}/{instituteName}")
    public @ResponseBody InstituteDetail getInstituteById(
            @PathVariable("instituteId") @Min(1) Long instituteId,
            @PathVariable("instituteName") @NotBlank String instituteName,
            @RequestParam(name = "field_group", required = false) String fieldGroup,
            @RequestParam(name = "fields", required = false) List<String> fields,
            @RequestHeader(value = "x-user-id", required = false) Long userId,
            @RequestHeader(value = "fe_client", required = false) Client client,
            @RequestHeader(value = "derived_attributes", required = false, defaultValue = "true")
                    Boolean derivedAttributes,
            @RequestHeader(value = "cut_offs", required = false, defaultValue = "true")
                    Boolean cutOffs,
            @RequestHeader(value = "facilities", required = false, defaultValue = "true")
                    Boolean facilities,
            @RequestHeader(value = "gallery", required = false, defaultValue = "true")
                    Boolean gallery,
            @RequestHeader(value = "placements", required = false, defaultValue = "true")
                    Boolean placements,
            @RequestHeader(value = "notable_alumni", required = false, defaultValue = "true")
                    Boolean notableAlumni,
            @RequestHeader(value = "sections", required = false, defaultValue = "true")
                    Boolean sections,
            @RequestHeader(value = "widgets", required = false, defaultValue = "true")
                    Boolean widgets,
            @RequestHeader(value = "courses_per_degree", required = false, defaultValue = "true")
                    Boolean coursesPerDegree,
            @RequestHeader(value = "campus_engagement", required = false, defaultValue = "true")
                    Boolean campusEngagementFlag)
            throws Exception {
        exploreValidator.validateFieldAndFieldGroup(fields, fieldGroup);
        return instituteDetailService
                .getDetail(instituteId, instituteName, userId, fieldGroup, fields, client,
                        derivedAttributes, cutOffs, facilities, gallery, placements, notableAlumni,
                        sections, widgets, coursesPerDegree, campusEngagementFlag);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/course/{courseId}/{courseName}")
    public @ResponseBody CourseDetail getCourseById(@PathVariable("courseId") @Min(1) long courseId,
            @PathVariable @NotBlank String courseName,
            @RequestParam(name = "field_group", required = false) String fieldGroup,
            @RequestParam(name = "fields", required = false) List<String> fields,
            @RequestHeader(value = "x-user-id", required = false) Long userId,
            @RequestHeader(value = "fe_client", required = false) Client client,
            @RequestHeader(value = "course_fees", required = false, defaultValue = "true")
                    Boolean courseFees,
            @RequestHeader(value = "institute", required = false, defaultValue = "true")
                    Boolean institute,
            @RequestHeader(value = "widgets", required = false, defaultValue = "true")
                    Boolean widgets,
            @RequestHeader(value = "derived_attributes", required = false, defaultValue = "true")
                    Boolean derivedAttributes,
            @RequestHeader(value = "exams_accepted", required = false, defaultValue = "true")
                    Boolean examAccepted) {
        exploreValidator.validateFieldAndFieldGroup(fields, fieldGroup);
        return courseDetailService
                .getDetail(courseId, courseName, userId, fieldGroup, fields, client, courseFees,
                        institute, widgets, derivedAttributes, examAccepted);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/school/{schoolId}/{schoolName}")
    public @ResponseBody SchoolDetail getSchoolById(@PathVariable("schoolId") @Min(1) long schoolId,
                                @PathVariable("schoolName") String schoolName,
                                @RequestParam(name = "field_group", required = false) String fieldGroup,
                                @RequestParam(name = "fields", required = false) List<String> fields,
                                @RequestHeader(value = "x-user-id", required = false) Long userId,
                                @RequestHeader(value = "fe_client", required = false, defaultValue = "WEB")
                                        Client client) {
        exploreValidator.validateFieldAndFieldGroup(fields, fieldGroup);
        return schoolService
                .getSchoolDetails(schoolId, client, schoolName, fields, fieldGroup, userId);
    }

}
