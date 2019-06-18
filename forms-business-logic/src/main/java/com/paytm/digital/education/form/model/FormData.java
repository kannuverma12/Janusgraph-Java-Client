package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotEmpty;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormData {

    @Id
    @JsonProperty("refId")
    private String id;

    @Field("transactionType")
    private String transactionType;

    @Field("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt = new Date();

    @Field("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    @Field("expiryDate")
    private Date expiryDate;

    @Field("candidateId")
    private String candidateId;

    @Field("mobileVerified")
    private Boolean mobileVerified;

    @Field("emailVerified")
    private Boolean emailVerified;

    @Field("customerId")
    private String customerId;

    @Field("merchantId")
    private String merchantId;

    @Field("merchantProductId")
    private String merchantProductId;

    @Field("merchantName")
    private String merchantName;

    @Field("status")
    private FormStatus status;

    @Field("formFulfilment")
    private FormFulfilment formFulfilment;

    @Field("candidateDetails")
    private CandidateDetails candidateDetails;

    @Field("merchantCandidateId")
    private String merchantCandidateId;

    @Field("additionalData")
    private Map<String, Object> additionalData;

    @Field("dmsDocs")
    private Set<DMSDoc> dmsDocs;

}
