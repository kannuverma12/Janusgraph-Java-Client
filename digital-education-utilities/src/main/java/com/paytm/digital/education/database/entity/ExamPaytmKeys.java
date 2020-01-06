package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExamPaytmKeys extends PaytmKeys {

    private static final long serialVersionUID = 8460070816928343174L;

    @JsonProperty("college_predictor_id")
    @Field("college_predictor_id")
    private Long collegePredictorId;

    @Field("form_id")
    @JsonProperty("form_id")
    private String formId;

    @Field("webFormUriPrefix")
    private String webFormUriPrefix;

    @Field("terms_and_conditions")
    @JsonProperty("terms_and_conditions")
    private String termsAndConditions;

    @Field("privacy_policies")
    @JsonProperty("privacy_policies")
    private String privacyPolicies;

    @Field("disclaimer")
    @JsonProperty("disclaimer")
    private String disclaimer;

    @Field("registration_guidelines")
    @JsonProperty("registration_guidelines")
    private String registrationGuidelines;

}
