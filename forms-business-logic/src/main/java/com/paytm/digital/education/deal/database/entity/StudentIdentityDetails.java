package com.paytm.digital.education.deal.database.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

import static com.paytm.digital.education.deal.constants.DealConstant.YYYY_MM_DD;
import static com.paytm.digital.education.deal.constants.DealConstant.YYYY_MM_DD_T_HH_MM_SS;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentIdentityDetails {
    @Field("id_no")
    @JsonProperty("id_no")
    private String idNo;

    @Field("valid_upto")
    @JsonProperty("valid_upto")
    @JsonFormat(pattern = YYYY_MM_DD)
    private Date validUpto;

    @Field("issuer")
    @JsonProperty("issuer")
    private String issuer;

    @Field("issue_date")
    @JsonProperty("issue_date")
    @JsonFormat(pattern = YYYY_MM_DD)
    private Date issueDate;

    @Field("verified")
    @JsonProperty("verified")
    private Boolean verified;

    @Field("student_id_url")
    @JsonProperty("student_id_url")
    private String studentIdUrl;

    @Field("verification_date")
    @JsonProperty("verification_date")
    @JsonFormat(pattern = YYYY_MM_DD_T_HH_MM_SS)
    private Date verificationDate;

    @Field("verified_by")
    @JsonProperty("verified_by")
    private String verifiedBy;
}
