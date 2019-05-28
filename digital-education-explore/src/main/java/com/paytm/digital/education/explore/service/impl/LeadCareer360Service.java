package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.database.entity.Lead;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.request.thirdparty.Career360LeadRequest;
import com.paytm.digital.education.explore.response.dto.lead.Career360LeadResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
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
