package com.paytm.digital.education.explore.controller;


import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.response.dto.detail.CompareCourseDetail;
import com.paytm.digital.education.explore.response.dto.detail.CompareDetail;
import com.paytm.digital.education.explore.service.CompareCourseService;
import com.paytm.digital.education.explore.service.CompareService;
import com.paytm.digital.education.explore.validators.ExploreValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Objects;
import java.util.Arrays;

import com.paytm.digital.education.mapping.ErrorEnum;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@RestController
@Validated
@RequestMapping(EDUCATION_BASE_URL)
public class CompareController {

    private CompareService       compareService;
    private CompareCourseService compareCourseService;
    private ExploreValidator     exploreValidator;

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/compare/institute")
    public @ResponseBody CompareDetail compareInstitutes(
            @RequestParam(name = "inst1") @Min(1) Long inst1,
            @RequestParam(name = "instName1") @NotBlank String instName1,
            @RequestParam(name = "inst2") @Min(1) Long inst2,
            @RequestParam(name = "instName2") @NotBlank String instName2,
            @RequestParam(name = "inst3", required = false) Long inst3,
            @RequestParam(name = "instName3", required = false) String instName3,
            @RequestParam(name = "field_group", required = false) String fieldGroup,
            @RequestParam(name = "fields", required = false) List<String> fields,
            @RequestHeader(value = "x-user-id") @Min(1) @NotNull Long userId) throws Exception {

        exploreValidator.validateFieldAndFieldGroup(fields, fieldGroup);
        Map<Long, String> instKeyMap = new HashMap<>();
        instKeyMap.put(inst1, instName1);
        instKeyMap.put(inst2, instName2);
        if (Objects.nonNull(inst3) || Objects.nonNull(instName3)) {
            if (Objects.isNull(inst3) || Objects.isNull(instName3)) {
                throw new BadRequestException(ErrorEnum.INSTITUTE_NAME_OR_ID_MISSING,
                        ErrorEnum.INSTITUTE_NAME_OR_ID_MISSING.getExternalMessage());
            }
            instKeyMap.put(inst3, instName3);
        }
        return compareService.compareInstitutes(instKeyMap, fieldGroup, fields, userId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/compare/courses")
    public @ResponseBody List<CompareCourseDetail> compareCourses(
            @RequestHeader(value = "x-user-id") @Min(1) @NotNull Long userId,
            @RequestParam(name = "course1") @Min(1) @NotNull Long course1,
            @RequestParam(name = "course2") @Min(1) @NotNull Long course2,
            @RequestParam(name = "course3", required = false) @Min(1) Long course3,
            @RequestParam(name = "field_group", required = false) String fieldGroup,
            @RequestParam(name = "fields", required = false) List<String> fields) throws Exception {

        List<Long> courseList = Arrays.asList(course1, course2, course3);
        exploreValidator.validateFieldAndFieldGroup(fields, fieldGroup);

        return compareCourseService.compareCourses(courseList, fieldGroup, fields);
    }
}
