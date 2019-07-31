package com.paytm.digital.education.deal.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.deal.database.entity.DealsStudentData;
import com.paytm.digital.education.deal.enums.StudentStatus;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerificationStatusResponse {

    @JsonProperty("student_status")
    private StudentStatus status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("student_details")
    private DealsStudentData dealsStudentData;
}
