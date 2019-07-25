package com.paytm.digital.education.deal.database.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.deal.enums.StatusType;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

import static com.paytm.digital.education.deal.constants.DealConstant.YYYY_MM_DD;
import static com.paytm.digital.education.deal.constants.DealConstant.YYYY_MM_DD_T_HH_MM_SS;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document("deals_eligible_student_data")
public class DealsEligibleStudentData {

    @Id
    @JsonIgnore
    @Field("_id")
    ObjectId id;

    @Field("customer_id")
    @JsonProperty("customer_id")
    private Long customerId;

    @Field("name")
    @JsonProperty("name")
    private String name;

    @Field("email")
    @JsonProperty("email")
    private String email;

    @Field("mobile_no")
    @JsonProperty("mobile_no")
    private String mobileNo;

    @Field("date_of_birth")
    @JsonProperty("date_of_birth")
    @JsonFormat(pattern = YYYY_MM_DD)
    private Date dateOfBirth;

    @Field("institute_name")
    @JsonProperty("institute_name")
    private String instituteName;

    @Field("year_of_passing")
    @JsonProperty("year_of_passing")
    private String yearOfPassing;

    @Field("stream")
    @JsonProperty("stream")
    private String stream;

    @Field("mobile_verified")
    @JsonProperty("mobile_verified")
    private Boolean mobileVerified;

    @Field("email_verified")
    @JsonProperty("email_verified")
    private Boolean emailVerified;

    @Field("student_id_details")
    @JsonProperty("student_id_details")
    private StudentIdentityDetails studentIdentityDetails;

    @Field("status")
    @JsonProperty("status")
    private StatusType status;

    @Field("created_at")
    @JsonProperty("created_at")
    @JsonFormat(pattern = YYYY_MM_DD_T_HH_MM_SS)
    private Date createdAt;

    @Field("updated_at")
    @JsonProperty("updated_at")
    @JsonFormat(pattern = YYYY_MM_DD_T_HH_MM_SS)
    private Date updatedAt;
}
