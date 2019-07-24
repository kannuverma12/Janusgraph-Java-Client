package com.paytm.digital.education.deal.database.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.deal.enums.StatusType;
import com.paytm.digital.education.deal.response.dto.ResponseDto;
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
public class DealsEligibleStudentData extends ResponseDto {

    @Id
    @JsonIgnore
    @Field("ref_id")
    ObjectId refId;

    @Field("customer_id")
    @JsonProperty("customer_id")
    private Long customerId;

    @Field("email")
    @JsonProperty("email")
    private String email;

    @Field("mobile_no")
    @JsonProperty("mobile_no")
    private String mobileNo;

    @Field("first_name")
    @JsonProperty("first_name")
    private String firstName;

    @Field("middle_name")
    @JsonProperty("middle_name")
    private String middleName;

    @Field("last_name")
    @JsonProperty("last_name")
    private String lastName;

    @Field("date_of_birth")
    @JsonProperty("date_of_birth")
    @JsonFormat(pattern = YYYY_MM_DD)
    private Date dateOfBirth;

    @Field("mobile_verified")
    @JsonProperty("mobile_verified")
    private Boolean mobileVerified;

    @Field("email_verified")
    @JsonProperty("email_verified")
    private Boolean emailVerified;

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

    @Field("student_id_details")
    @JsonProperty("student_id_details")
    private StudentIdentityDetails studentIdentityDetails;

    @Field("institute_name")
    @JsonProperty("institute_name")
    private String instituteName;

    @Field("age")
    @JsonProperty("age")
    private Integer age;

    @Field("gender")
    @JsonProperty("gender")
    private String gender;

    @Field("blood_group")
    @JsonProperty("blood_group")
    private String bloodGroup;

    @Field("level_of_student")
    @JsonProperty("level_of_student")
    private String levelOfStudent;

    @Field("year_and_batch")
    @JsonProperty("year_and_batch")
    private String yearAndBatch;

    @Field("stream")
    @JsonProperty("stream")
    private String stream;

    @Field("roll_no")
    @JsonProperty("roll_no")
    private String rollNo;

}
