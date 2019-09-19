package com.paytm.digital.education.coaching.ingestion.model.googleform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.coaching.ingestion.model.GoogleSheetColumnName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoachingInstituteForm {

    @JsonProperty("institute_id")
    @GoogleSheetColumnName("Institute Id")
    private Long instituteId;

    @JsonProperty("brand_name")
    @GoogleSheetColumnName("Brand Name")
    private String brandName;

    @JsonProperty("about_institute")
    @GoogleSheetColumnName("About Institute")
    private String aboutInstitute;

    @JsonProperty("address")
    @GoogleSheetColumnName("Address")
    private String address;

    @JsonProperty("city")
    @GoogleSheetColumnName("City")
    private String city;

    @JsonProperty("state")
    @GoogleSheetColumnName("State")
    private String state;

    @JsonProperty("pincode")
    @GoogleSheetColumnName("Pincode")
    private String pincode;

    @JsonProperty("latitude")
    @GoogleSheetColumnName("Latitude")
    private Double latitude;

    @JsonProperty("longitude")
    @GoogleSheetColumnName("Longitude")
    private Double longitude;

    @JsonProperty("email_id")
    @GoogleSheetColumnName("Email Id")
    private String emailId;

    @JsonProperty("phone_number")
    @GoogleSheetColumnName("Phone Number")
    private String phoneNumber;

    @JsonProperty("cover_image")
    @GoogleSheetColumnName("Cover Image")
    private String coverImage;

    @JsonProperty("logo")
    @GoogleSheetColumnName("Logo")
    private String logo;

    @JsonProperty("stream_ids")
    @GoogleSheetColumnName("Stream Ids")
    private String streamIds;

    @JsonProperty("exam_ids")
    @GoogleSheetColumnName("Exam Ids")
    private String examIds;

    @JsonProperty("course_types")
    @GoogleSheetColumnName("Course Types")
    private String courseTypes;

    @JsonProperty("year_of_establishment")
    @GoogleSheetColumnName("Year Of Establishment")
    private String yearOfEstablishment;

    @JsonProperty("brochure")
    @GoogleSheetColumnName("Brochure")
    private String brochure;

    @JsonProperty("level_of_education")
    @GoogleSheetColumnName("Level Of Education")
    private String levelOfEducation;

    @JsonProperty("steps_to_apply")
    @GoogleSheetColumnName("Steps To Apply")
    private String stepsToApply;

    @JsonProperty("scholarship_matrix")
    @GoogleSheetColumnName("Scholarship Matrix")
    private String scholarshipMatrix;

    @JsonProperty("highlight_attribute_name1")
    @GoogleSheetColumnName("Highlight Attribute Name1")
    private String highlightAttributeName1;

    @JsonProperty("highlight_value1")
    @GoogleSheetColumnName("Highlight Value1")
    private String highlightValue1;

    @JsonProperty("highlight_logo1")
    @GoogleSheetColumnName("Highlight Logo1")
    private String highlightLogo1;

    @JsonProperty("highlight_attribute_name2")
    @GoogleSheetColumnName("Highlight Attribute Name2")
    private String highlightAttributeName2;

    @JsonProperty("highlight_value2")
    @GoogleSheetColumnName("Highlight Value2")
    private String highlightValue2;

    @JsonProperty("highlight_logo2")
    @GoogleSheetColumnName("Highlight Logo2")
    private String highlightLogo2;

    @JsonProperty("highlight_attribute_name3")
    @GoogleSheetColumnName("Highlight Attribute Name3")
    private String highlightAttributeName3;

    @JsonProperty("highlight_value3")
    @GoogleSheetColumnName("Highlight Value3")
    private String highlightValue3;

    @JsonProperty("highlight_logo3")
    @GoogleSheetColumnName("Highlight Logo3")
    private String highlightLogo3;

    @JsonProperty("highlight_attribute_name4")
    @GoogleSheetColumnName("Highlight Attribute Name4")
    private String highlightAttributeName4;

    @JsonProperty("highlight_value4")
    @GoogleSheetColumnName("Highlight Value4")
    private String highlightValue4;

    @JsonProperty("highlight_logo4")
    @GoogleSheetColumnName("Highlight Logo4")
    private String highlightLogo4;

    @JsonProperty("faq1")
    @GoogleSheetColumnName("FAQ1")
    private String faq1;

    @JsonProperty("faq_ans1")
    @GoogleSheetColumnName("FAQ Ans1")
    private String faqAns1;

    @JsonProperty("faq2")
    @GoogleSheetColumnName("FAQ2")
    private String faq2;

    @JsonProperty("faq_ans2")
    @GoogleSheetColumnName("FAQ Ans2")
    private String faqAns2;

    @JsonProperty("faq3")
    @GoogleSheetColumnName("FAQ3")
    private String faq3;

    @JsonProperty("faq_ans3")
    @GoogleSheetColumnName("FAQ Ans3")
    private String faqAns3;

    @JsonProperty("paytm_merchant_id")
    @GoogleSheetColumnName("Paytm Merchant Id")
    private String paytmMerchantId;

    @JsonProperty("paytm_product_id")
    @GoogleSheetColumnName("Paytm Product Id")
    private String paytmProductId;

    @JsonProperty("status_active")
    @GoogleSheetColumnName("Status Active")
    private String statusActive;

    @JsonProperty("global_priority")
    @GoogleSheetColumnName("Global Priority")
    private Integer globalPriority;

}
