package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class FormData {

    @Id
    @JsonProperty("refId")
    private String id;

    @Field("fullName")
    private String fullName;

    @Field("email")
    private String email;

    @Field("mobileNumber")
    private String mobileNumber;

    @Field("dateOfBirth")
    private String dateOfBirth;

    @Field("password")
    private String password;

    @Field("salutation")
    private String salutation;

    @Field("username")
    private String username;

    @Field("firstName")
    private String firstName;

    @Field("middleName")
    private String middleName;

    @Field("lastName")
    private String lastName;

    @Field("mothersName")
    private String mothersName;

    @Field("fathersName")
    private String fathersName;

    @Field("gender")
    private String gender;

    @Field("age")
    private Integer age;

    @Field("landLineNumber")
    private String landLineNumber;

    @Field("isMarried")
    private Boolean isMarried;

    @Field("isPwd")
    private Boolean isPwd;

    @Field("religion")
    private String religion;

    @Field("category")
    private String category;

    @Field("nationality")
    private String nationality;

    @Field("merchant")
    private String merchant;

    @Field("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @Field("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
