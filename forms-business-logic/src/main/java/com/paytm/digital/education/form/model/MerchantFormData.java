package com.paytm.digital.education.form.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MerchantFormData {
    private String id;

    @Field("orderId")
    private Long orderId;

    @Field("fullName")
    private String fullName;

    @Field("email")
    private String email;

    @Field("mobileNumber")
    private String mobileNumber;

    @Field("dateOfBirth")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date dateOfBirth;

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


    public MerchantFormData(FormData formData) {
        this.id = formData.getId();
        this.orderId = formData.getFormFulfilment().getOrderId();
        this.fullName = formData.getCandidateDetails().getFullName();
        this.email = formData.getCandidateDetails().getEmail();
        this.mobileNumber = formData.getCandidateDetails().getMobileNumber();
        this.dateOfBirth = formData.getCandidateDetails().getDateOfBirth();
        this.firstName = formData.getCandidateDetails().getFirstName();
        this.middleName = formData.getCandidateDetails().getMiddleName();
        this.lastName = formData.getCandidateDetails().getLastName();
        this.mothersName = formData.getCandidateDetails().getMotherName();
        this.fathersName = formData.getCandidateDetails().getFatherName();
        this.gender = formData.getCandidateDetails().getGender();
        this.age = formData.getCandidateDetails().getAge();
        this.landLineNumber = formData.getCandidateDetails().getLandLineNumber();
        this.isMarried = formData.getCandidateDetails().getIsMarried();
        this.isPwd = formData.getCandidateDetails().getIsPwd();
        this.religion = formData.getCandidateDetails().getReligion();
        this.category = formData.getCandidateDetails().getCategory();
        this.nationality = formData.getCandidateDetails().getNationality();
        this.merchant = formData.getMerchantName();
        this.createdAt = formData.getCreatedAt();
        this.updatedAt = formData.getUpdatedAt();
        this.expiryDate = formData.getExpiryDate();
        this.candidateId = formData.getCandidateId();
        this.mobileVerified = formData.getMobileVerified();
        this.emailVerified = formData.getEmailVerified();
        this.customerId = formData.getCustomerId();
        this.merchantId = formData.getMerchantId();
    }

}
