package com.paytm.digital.education.coaching.ingestion.model.googleform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingInstituteForm {

    private Long   instituteId;
    private String brandName;
    private String aboutInstitute;

    private String address;
    private String city;
    private String state;
    private String pincode;
    private Double latitude;
    private Double longitude;
    private String emailId;
    private String phoneNumber;

    private String coverImage;
    private String logo;

    private String streamIds;
    private String examIds;
    private String courseTypes;
    private String yearOfEstablishment;
    private String brochure;
    private String isEnabled;
    private String courseLevel;
    private String levelOfEducation;
    private String stepsToApply;
    private String scholarshipMatrix;

    private String highlightAttributeName1;
    private String highlightValue1;
    private String highlightLogo1;
    private String highlightAttributeName2;
    private String highlightValue2;
    private String highlightLogo2;
    private String highlightAttributeName3;
    private String highlightValue3;
    private String highlightLogo3;
    private String highlightAttributeName4;
    private String highlightValue4;
    private String highlightLogo4;

    private String faq1;
    private String faqAns1;
    private String faq2;
    private String faqAns2;
    private String faq3;
    private String faqAns3;

    private String paytmMerchantId;
    private String paytmProductId;

    private String  statusActive;
    private Integer globalPriority;

}
