package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.explore.database.entity.Lead;
import com.paytm.digital.education.explore.database.entity.UserDetails;
import com.paytm.digital.education.explore.service.LeadService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;


import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

@Slf4j
@RestController
@RequestMapping(EDUCATION_BASE_URL)
@AllArgsConstructor
@Validated
public class LeadController {
    private LeadService           leadService;

    @PostMapping("/auth/v1/lead")
    public com.paytm.digital.education.explore.response.dto.common.Lead captureLead(
            @Valid @RequestBody Lead lead,
            @RequestHeader("x-user-id") long userId) {
        log.info("Lead Request : {}", JsonUtils.toJson(lead));
        lead.setUserId(userId);
        return leadService.captureLead(lead);
    }

    @PostMapping("/auth/v1/unfollow")
    public com.paytm.digital.education.explore.response.dto.common.Lead unfollowLead(
            @RequestBody Lead lead, @RequestHeader("x-user-id") long userId) {
        log.info("Unfollow Request : {}", JsonUtils.toJson(lead));
        lead.setUserId(userId);
        return leadService.unfollowLead(lead);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/user_details")
    public UserDetails getLeadUserDetails(@RequestHeader("x-user-id") @NotNull @Min(1) Long userId,
            @RequestHeader(value = "x-user-email", required = false) String email,
            @RequestHeader(value = "x-user-firstname", required = false) String firstName ,
            @RequestHeader(value = "x-user-phone", required = false) String phone) {
        log.info("Received email : {}, name : {}, phone : {}", email, firstName, phone);
        return leadService.getUserDetails(userId, email, firstName, phone);
    }

}
