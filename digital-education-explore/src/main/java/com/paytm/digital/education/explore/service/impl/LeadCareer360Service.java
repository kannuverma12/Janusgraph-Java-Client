package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.database.entity.BaseLeadResponse;
import com.paytm.digital.education.explore.database.entity.Lead;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.thirdparty.lead.Career360LeadRequest;
import com.paytm.digital.education.explore.thirdparty.lead.Career360LeadResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@AllArgsConstructor
public class LeadCareer360Service {

    private BaseRestApiService restApiService;

    public void send(Lead lead) {
        Career360LeadRequest career360LeadRequest = buildRequest(lead);
        try {
            Career360LeadResponse response = restApiService
                    .post("https://www.careers360.net/dj-api/paytm-user",
                            Career360LeadResponse.class,
                            career360LeadRequest, getHeaders());
            System.out.println(response.toString());
            if (Objects.isNull(lead.getBaseLeadResponse())) {
                List<BaseLeadResponse> leadResponses = new ArrayList<>();
                leadResponses.add(buildResponse(response));
                lead.setBaseLeadResponse(leadResponses);
            } else {
                lead.getBaseLeadResponse().add(buildResponse(response));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BaseLeadResponse buildResponse(Career360LeadResponse c360response) {
        if (Objects.nonNull(c360response)) {
            com.paytm.digital.education.explore.database.entity.Career360LeadResponse
                    baseLeadResponse =
                    new com.paytm.digital.education.explore.database.entity.Career360LeadResponse();
            baseLeadResponse.setCtaMessage(c360response.getCtaMessage());
            baseLeadResponse.setCtaStatus(c360response.getCtaStatus());
            baseLeadResponse.setErrorCode(c360response.getErrorCode());
            baseLeadResponse.setMessage(c360response.getMessage());
            return baseLeadResponse;
        }
        return null;
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        return headers;
    }

    private Career360LeadRequest buildRequest(Lead lead) {
        Career360LeadRequest career360LeadRequest = new Career360LeadRequest();
        career360LeadRequest.setName(lead.getContactName());
        career360LeadRequest.setEmail(lead.getContactEmail());
        career360LeadRequest.setStateId(lead.getStateId());
        career360LeadRequest.setCityId(lead.getCityId());
        career360LeadRequest.setMobile(lead.getContactNumber());
        career360LeadRequest
                .setEntityType(EducationEntity.convertToCareer360entity(lead.getEntityType()));
        career360LeadRequest.setEntityId(lead.getEntityId());
        career360LeadRequest.setRequestType(lead.getRequestType());
        career360LeadRequest.setPaytmCustomerId(lead.getUserId());
        return career360LeadRequest;
    }
}
