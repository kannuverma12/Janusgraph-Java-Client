package com.paytm.digital.education.explore.controller;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

import com.paytm.digital.education.explore.response.dto.detail.InstituteDetail;
import com.paytm.digital.education.explore.service.impl.InstituteDetailServiceImpl;
import com.paytm.digital.education.explore.validators.ExploreValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(EDUCATION_BASE_URL)
public class DetailsApiController {

    private InstituteDetailServiceImpl instituteDetailService;
    private ExploreValidator           exploreValidator;

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/institute/{instituteId}")
    public @ResponseBody InstituteDetail getInstituteById(
            @PathVariable("instituteId") long instituteId,
            @RequestParam(name = "field_group", required = false) String fieldGroup,
            @RequestParam(name = "fields", required = false) List<String> fields,
            @RequestHeader(value = "x-user-id", required = false) Long userId) {

        exploreValidator.validateFieldAndFieldGroup(fields, fieldGroup);

        return instituteDetailService
                .getDetail(instituteId, userId, fieldGroup, fields);
    }
}
