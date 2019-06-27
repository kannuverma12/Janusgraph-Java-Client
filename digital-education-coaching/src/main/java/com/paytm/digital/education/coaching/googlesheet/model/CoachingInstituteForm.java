package com.paytm.digital.education.coaching.googlesheet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoachingInstituteForm {

    @JsonProperty("institute_id")
    private Long instituteId;

    @JsonProperty("institute_name")
    private String instituteName;

    @JsonProperty("brand_name")
    private String brandName;

    @JsonProperty("logo")
    private String logo;

    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("cover_image")
    private String coverImage;

    @JsonProperty("about_institute")
    private String aboutInstitute;

    @JsonProperty("streams_prepared")
    private String streamPrepared;

    @JsonProperty("exam_prepared_(_ids_)")
    private String examPreparedIds;

    @JsonProperty("courses_available")
    private String coursesAvailable;

    @JsonProperty("scholarship_matrix")
    private String scholarshipMatrix;

    @JsonProperty("brochure")
    private String brochure;

    @JsonProperty("scholarship_exam")
    private String scholarshipExam;

    @JsonProperty("steps_to_apply")
    private String stepsToApply;

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("address_line_1")
    private String addressLine1;

    @JsonProperty("address_line_2")
    private String addressLine2;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("pincode")
    private String pincode;

    @JsonProperty("primary_phone_number")
    private String primaryPhoneNumber;

    @JsonProperty("primary_email_id")
    private String primaryEmailId;

    @JsonProperty("latitude")
    private String latitude;

    @JsonProperty("longitude")
    private String longitude;

    @JsonProperty("year_of_establishment")
    private Integer yearOfEstablishment;

    @JsonProperty("status")
    private String status;

    @JsonProperty("city_/_state_presence")
    private String cityStatePresence;

    @JsonProperty("highlight_1_:_attribute_name")
    private String highlight1AttributeName;

    @JsonProperty("highlight_1_:_value")
    private String highlight1Value;

    @JsonProperty("highlight_2_:_attribute_name")
    private String highlight2AttributeName;

    @JsonProperty("highlight_2_:_value")
    private String highlight2Value;

    @JsonProperty("highlight_3_:_attribute_name")
    private String highlight3AttributeName;

    @JsonProperty("highlight_3_:_value")
    private String highlight3Value;

    @JsonProperty("highlight_4_:_attribute_name")
    private String highlight4AttributeName;

    @JsonProperty("highlight_4_:_value")
    private String highlight4Value;

    @JsonProperty("highlight_5_:_attribute_name")
    private String highlight5AttributeName;

    @JsonProperty("highlight_5_:_value")
    private String highlight5Value;

    @JsonProperty("highlight_6_:_attribute_name")
    private String highlight6AttributeName;

    @JsonProperty("highlight_6_:_value")
    private String highlight6Value;

    @JsonProperty("highlight_7_:_attribute_name")
    private String highlight7AttributeName;

    @JsonProperty("highlight_7_:_value")
    private String highlight7Value;

    @JsonProperty("highlight_8_:_attribute_name")
    private String highlight8AttributeName;

    @JsonProperty("highlight_8_:_value")
    private String highlight8Value;

    @JsonProperty("highlight_9_:_attribute_name")
    private String highlight9AttributeName;

    @JsonProperty("highlight_9_:_value")
    private String highlight9Value;

    @JsonProperty("highlight_10_:_attribute_name")
    private String highlight10AttributeName;

    @JsonProperty("highlight_10_:_value")
    private String highlight10Value;
}
