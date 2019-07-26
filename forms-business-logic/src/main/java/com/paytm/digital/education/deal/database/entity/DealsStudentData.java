package com.paytm.digital.education.deal.database.entity;

import static com.paytm.digital.education.deal.constants.DealConstant.YYYY_MM_DD;
import static com.paytm.digital.education.deal.constants.DealConstant.YYYY_MM_DD_T_HH_MM_SS;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.deal.enums.StudentStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document("deals_student_data")
public class DealsStudentData {

    @Id
    @Field("_id")
    @JsonProperty("refId")
    String refId;

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

    @Field("student_identity")
    @JsonProperty("student_identity")
    private StudentIdentity studentIdentity;

    @Field("status")
    @JsonProperty("status")
    private StudentStatus status;

    @Field("created_at")
    @JsonProperty("created_at")
    @JsonFormat(pattern = YYYY_MM_DD_T_HH_MM_SS)
    private Date createdAt;

    @Field("updated_at")
    @JsonProperty("updated_at")
    @JsonFormat(pattern = YYYY_MM_DD_T_HH_MM_SS)
    private Date updatedAt;
}
