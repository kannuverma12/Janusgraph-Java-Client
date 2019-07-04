package com.paytm.digital.education.explore.controller;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

import java.util.List;

import com.paytm.digital.education.explore.enums.Client;
import com.paytm.digital.education.explore.response.dto.detail.CourseDetail;
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
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Slf4j
@AllArgsConstructor
@RestController
@Validated
@RequestMapping(EDUCATION_BASE_URL)
public class DetailsApiController {

    private InstituteDetailServiceImpl instituteDetailService;
    private CourseDetailServiceImpl    courseDetailService;
    private ExamDetailServiceImpl      examDetailService;
    private ExploreValidator           exploreValidator;

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/exam/{examId}/{examName}")
    public @ResponseBody ExamDetail getExamById(
            @PathVariable("examId") @Min(1) Long examId,
            @PathVariable("examName") @NotBlank String examName,
            @RequestParam(name = "field_group", required = false) String fieldGroup,
            @RequestParam(name = "fields", required = false) List<String> fields,
            @RequestHeader(value = "x-user-id", required = false) Long userId) throws Exception {
        exploreValidator.validateFieldAndFieldGroup(fields, fieldGroup);
        return examDetailService
                .getDetail(examId, examName, userId, fieldGroup, fields);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/institute/{instituteId}/{instituteName}")
    public @ResponseBody InstituteDetail getInstituteById(
            @PathVariable("instituteId") @Min(1) Long instituteId,
            @PathVariable("instituteName") @NotBlank String instituteName,
            @RequestParam(name = "field_group", required = false) String fieldGroup,
            @RequestParam(name = "fields", required = false) List<String> fields,
            @RequestHeader(value = "x-user-id", required = false) Long userId,
            @RequestHeader(value = "request-client", required = false) Client client)
            throws Exception {
        exploreValidator.validateFieldAndFieldGroup(fields, fieldGroup);
        return instituteDetailService
                .getDetail(instituteId, instituteName, userId, fieldGroup, fields, client);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/course/{courseId}/{courseName}")
    public @ResponseBody CourseDetail getCourseById(@PathVariable("courseId") @Min(1) long courseId,
            @PathVariable @NotBlank String courseName,
            @RequestParam(name = "field_group", required = false) String fieldGroup,
            @RequestParam(name = "fields", required = false) List<String> fields,
            @RequestHeader(value = "x-user-id", required = false) Long userId) {
        exploreValidator.validateFieldAndFieldGroup(fields, fieldGroup);
        return courseDetailService.getDetail(courseId, courseName, userId, fieldGroup, fields);
    }
}
