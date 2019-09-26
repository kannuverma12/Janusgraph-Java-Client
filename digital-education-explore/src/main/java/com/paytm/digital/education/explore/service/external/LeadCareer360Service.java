package com.paytm.digital.education.explore.service.external;

import com.paytm.digital.education.explore.database.entity.BaseLeadResponse;
import com.paytm.digital.education.explore.database.entity.Lead;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.enums.LeadAction;
import com.paytm.digital.education.explore.thirdparty.lead.Career360LeadRequest;
import com.paytm.digital.education.explore.thirdparty.lead.Career360LeadResponse;
import com.paytm.digital.education.explore.thirdparty.lead.Career360UnfollowRequest;
import com.paytm.digital.education.explore.thirdparty.lead.Career360UnfollowResponse;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LeadCareer360Service {

    private static final Logger log = LoggerFactory.getLogger(LeadCareer360Service.class);

    @Value("${thirdparty.explore.career360.lead.follow}")
    private String c360LeadFollow;

    @Value("${thirdparty.explore.career360.lead.unfollow}")
    private String c360LeadUnfollow;

    @Value("${thirdparty.explore.career360.lead.apikey}")
    private String apiKey;

    @Autowired
    private BaseRestApiService restApiService;

    public BaseLeadResponse sendUnfollow(Lead lead) {
        Career360UnfollowRequest career360UnfollowRequest = buildUnfollowRequest(lead);
        String jsonStr = JsonUtils.toJson(career360UnfollowRequest);
        Career360UnfollowResponse response = restApiService
                .post(c360LeadUnfollow, Career360UnfollowResponse.class, jsonStr, getHeaders());
        log.info("Careers360 lead response : {}", JsonUtils.toJson(response));
        return buildUnfollowResponse(response);
    }

    public BaseLeadResponse sendLead(Lead lead) {
        Career360LeadRequest career360LeadRequest = buildRequest(lead);
        String jsonStr = JsonUtils.toJson(career360LeadRequest);
        Career360LeadResponse response = restApiService
                .post(c360LeadFollow, Career360LeadResponse.class, jsonStr, getHeaders());
        log.info("Careers360 lead response : {}", JsonUtils.toJson(response));
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
        headers.put("x-api-token", apiKey);
        return headers;
    }

    private Career360LeadRequest buildRequest(Lead lead) {
        Career360LeadRequest career360LeadRequest = new Career360LeadRequest();
        career360LeadRequest.setName(lead.getContactName());
        career360LeadRequest.setEmail(lead.getContactEmail());
        career360LeadRequest.setStateId(lead.getStateId());
        career360LeadRequest.setCityId(lead.getCityId());
        career360LeadRequest.setMobile(lead.getContactNumber());
        career360LeadRequest.setActionLocation("");
        career360LeadRequest
                .setEntityType(EducationEntity.convertToCareer360entity(lead.getEntityType()));
        career360LeadRequest.setEntityId(lead.getEntityId());
        career360LeadRequest
                .setRequestType(LeadAction.getCareers360RequestType(lead.getAction()));
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
