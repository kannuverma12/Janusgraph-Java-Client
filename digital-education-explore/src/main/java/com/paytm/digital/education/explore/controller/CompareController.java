package com.paytm.digital.education.explore.controller;


import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

import com.paytm.digital.education.explore.response.dto.detail.CompareDetail;
import com.paytm.digital.education.explore.service.CompareService;
import com.paytm.digital.education.explore.validators.ExploreValidator;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@Validated
@RequestMapping(EDUCATION_BASE_URL)
public class CompareController {

    @Autowired
    private CompareService   compareService;
    private ExploreValidator exploreValidator;

    @RequestMapping(method = RequestMethod.GET, path = "/compare/v1/inst")
    public @ResponseBody CompareDetail compareInstitutes(
            @RequestParam(name = "inst1", required = true) Long inst1,
            @RequestParam(name = "inst2", required = true) Long inst2,
            @RequestParam(name = "inst3", required = false) Long inst3,
            @RequestParam(name = "field_group", required = false) String fieldGroup,
            @RequestParam(name = "fields", required = false) List<String> fields,
            @RequestHeader(value = "x-user-id", required = false) Long userId) throws Exception {

        List<Long> instList = new ArrayList<>();
        instList.add(inst1);
        instList.add(inst2);
        instList.add(inst3);
        exploreValidator.validateFieldAndFieldGroup(fields, fieldGroup);

        return compareService.compareInstitutes(instList, fieldGroup, fields);
    }

}
