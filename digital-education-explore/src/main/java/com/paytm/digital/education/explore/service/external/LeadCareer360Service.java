package com.paytm.digital.education.explore.service.external;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.database.entity.BaseLeadResponse;
import com.paytm.digital.education.explore.database.entity.Lead;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.enums.LeadAction;
import com.paytm.digital.education.explore.thirdparty.lead.Career360LeadRequest;
import com.paytm.digital.education.explore.thirdparty.lead.Career360LeadResponse;
import com.paytm.digital.education.explore.thirdparty.lead.Career360UnfollowRequest;
import com.paytm.digital.education.explore.thirdparty.lead.Career360UnfollowResponse;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class LeadCareer360Service {

    private BaseRestApiService restApiService;

    public BaseLeadResponse send(Lead lead) throws Exception {
        if (LeadAction.Unfollow.equals(lead.getAction())) {
            return sendUnfollow(lead);
        } else {
            return sendLead(lead);
        }
    }

    private BaseLeadResponse sendUnfollow(Lead lead) throws Exception {
        Career360UnfollowRequest career360UnfollowRequest = buildUnfollowRequest(lead);
        String jsonStr = JsonUtils.toJson(career360UnfollowRequest);
        Career360UnfollowResponse response = restApiService
                .post("https://www.careers360.net/dj-api/paytm-unfollow",
                        Career360UnfollowResponse.class, jsonStr,
                        getHeaders());
        log.info(response.toString());
        return buildUnfollowResponse(response);
    }

    private BaseLeadResponse sendLead(Lead lead) throws Exception {
        Career360LeadRequest career360LeadRequest = buildRequest(lead);
        String jsonStr = JsonUtils.toJson(career360LeadRequest);
        Career360LeadResponse response = restApiService
                .post("https://www.careers360.net/dj-api/paytm-user",
                        Career360LeadResponse.class,
                        jsonStr, getHeaders());
        return buildResponse(response);
    }

    private BaseLeadResponse buildResponse(Career360LeadResponse c360response) {
        BaseLeadResponse baseLeadResponse = new BaseLeadResponse();
        baseLeadResponse.setCtaMessage(c360response.getCtaMessage());
        baseLeadResponse.setLeadId(c360response.getLeadId());
        baseLeadResponse.setCtaCode(c360response.getCtaStatus());
        baseLeadResponse.setErrorCode(c360response.getErrorCode());
        baseLeadResponse.setMessage(c360response.getMessage());
        if ((c360response.getErrorCode() == 0 || c360response.getErrorCode() == 1) && (
                c360response.getCtaStatus() == 0 || c360response.getCtaStatus() == 2
                        || c360response.getCtaStatus() == 3
                        || c360response.getCtaStatus() == 4)) {
            baseLeadResponse.setInterested(true);
        }
        return baseLeadResponse;
    }

    private BaseLeadResponse buildUnfollowResponse(Career360UnfollowResponse c360response) {
        BaseLeadResponse baseLeadResponse = new BaseLeadResponse();
        baseLeadResponse.setErrorCode(c360response.getErrorCode());
        baseLeadResponse.setMessage(c360response.getErrorMessage());
        if (c360response.getErrorCode() == 0 || c360response.getErrorCode() == 3) {
            baseLeadResponse.setInterested(false);
        }
        return baseLeadResponse;
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        headers.put("x-api-token", "Pekfrtyuyuyerwdghjhff#54555hhfghfghfh");
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
        career360LeadRequest.setRequestType(LeadAction.getCareers360RequestType(lead.getAction()));
        career360LeadRequest.setPaytmCustomerId(lead.getUserId());
        return career360LeadRequest;
    }

    private Career360UnfollowRequest buildUnfollowRequest(Lead lead) {
        Career360UnfollowRequest career360UnfollowRequest = new Career360UnfollowRequest();
        career360UnfollowRequest.setEntityId(lead.getEntityId());
        career360UnfollowRequest
                .setEntityType(EducationEntity.convertToCareer360entity(lead.getEntityType()));
        career360UnfollowRequest.setPaytmCustomerId(lead.getUserId());
        return career360UnfollowRequest;
    }
}
