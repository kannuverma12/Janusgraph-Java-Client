package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.explore.database.entity.Lead;
import com.paytm.digital.education.explore.service.LeadService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

@RestController
@RequestMapping(EDUCATION_BASE_URL)
@AllArgsConstructor
public class LeadController {
    private LeadService leadService;

    @PostMapping("lead")
    public void captureLead(@RequestBody Lead lead,
                            @RequestHeader("x-user-id") String userId) {
        lead.setUserId(userId);
        leadService.captureLead(lead);
    }
}
