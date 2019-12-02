package com.paytm.digital.education.admin.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.database.entity.PaytmSourceData;
import com.paytm.digital.education.enums.EducationEntity;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaytmSourceDataResponse {
    private String status;

    @JsonProperty("entity")
    private EducationEntity educationEntity;

    @JsonProperty("success_data")
    private List<PaytmSourceData> paytmSourceData;
}
