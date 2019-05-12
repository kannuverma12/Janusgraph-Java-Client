package com.paytm.digital.education.form.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@Document
public class FormData {

    @Id
    @Field("_id")
    private String id;

    @Field("createdAt")
    private Date createdAt;

    @Field("updatedAt")
    private Date updatedAt;

    @Field("expiryDate")
    private Date expiryDate;

    @NotEmpty
    @Field("candidateId")
    private String candidateId;

    @Field("mobileVerified")
    private Boolean mobileVerified;

    @Field("emailVerified")
    private Boolean emailVerified;

    @NotEmpty
    @Field("customerId")
    private String customerId;

    @NotEmpty
    @Field("merchantId")
    private String merchantId;

    @Field("status")
    private String status;

    @Field("formFulfilment")
    private FormFulfilment formFulfilment;

    @Field("candidateDetails")
    private CandidateDetails candidateDetails;

    @Field("merchantCandidateId")
    private String merchantCandidateId;

}