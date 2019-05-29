package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.database.entity.Lead;
import com.paytm.digital.education.explore.enums.Career360EntityType;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.request.thirdparty.Career360LeadRequest;
import com.paytm.digital.education.explore.response.dto.lead.BaseLeadResponse;
import com.paytm.digital.education.explore.response.dto.lead.Career360LeadResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class LeadCareer360Service {

    private BaseRestApiService restApiService;

    public BaseLeadResponse send(Lead lead) {
        Career360LeadRequest career360LeadRequest = buildRequest(lead);
        try {
            Career360LeadResponse response = restApiService
                    .post("https://www.careers360.net/dj-api/paytm-user",
                            Career360LeadResponse.class,
                            career360LeadRequest, getHeaders());
            System.out.println(response.toString());
            return buildResponse(response, career360LeadRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private BaseLeadResponse buildResponse(Career360LeadResponse c360response, Career360LeadRequest c360request){
        if(Objects.nonNull(c360response)){
            BaseLeadResponse baseLeadResponse = new BaseLeadResponse();
            baseLeadResponse.setCtaMessage(c360response.getCtaMessage());
            baseLeadResponse.setCtaStatus(c360response.getCtaStatus());
            baseLeadResponse.setErrorCode(c360response.getErrorCode());
            baseLeadResponse.setMessage(c360response.getMessage());
            baseLeadResponse.setUserId(c360request.getPaytmCustomerId());
            baseLeadResponse.setEntityId(c360request.getEntityId());
            baseLeadResponse.setEntityType(Career360EntityType.convertToEducationEntity(c360request.getEntityType()));
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
