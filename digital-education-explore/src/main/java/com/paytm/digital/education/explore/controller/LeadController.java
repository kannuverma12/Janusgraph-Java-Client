package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.explore.database.entity.Lead;
import com.paytm.digital.education.explore.service.LeadService;

import javax.validation.Valid;

import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

@Slf4j
@RestController
@RequestMapping(EDUCATION_BASE_URL)
@AllArgsConstructor
@Validated
public class LeadController {
    private LeadService leadService;

    @PostMapping("/auth/v1/lead")
    public com.paytm.digital.education.explore.response.dto.common.Lead captureLead(
            @Valid @RequestBody Lead lead,
            @RequestHeader("x-user-id") long userId) throws Exception {
        log.info("Lead Request : {}", JsonUtils.toJson(lead));
        lead.setUserId(userId);
        leadService.captureLead(lead);
    }
}
