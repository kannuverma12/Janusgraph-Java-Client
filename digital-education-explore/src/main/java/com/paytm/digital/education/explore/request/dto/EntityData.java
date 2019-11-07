package com.paytm.digital.education.explore.request.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.EducationEntity;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntityData {

    @JsonProperty("entity_id")
    private Long entityId;

    @JsonProperty("college_predictor_id")
    private Long collegePredictorId;

    @JsonProperty("entity")
    private EducationEntity educationEntity;

    @JsonProperty("form_id")
    private String formId;

    @JsonProperty("webFormUriPrefix")
    private String webFormUriPrefix;

    @JsonProperty("pid")
    private Long pid;

    @JsonProperty("mid")
    private Long mid;

    @JsonProperty("terms_and_conditions")
    private String termsAndConditions;

    @JsonProperty("privacy_policies")
    private String privacyPolicies;

    @JsonProperty("disclaimer")
    private String disclaimer;

    @JsonProperty("registration_guidelines")
    private String registrationGuidelines;

    public static class Constants {

        public static final String MID = "mid";

        public static final String PID = "pid";

        public static final String COLLEGE_PREDICTOR_ID = "college_predictor_id";

    }


}
