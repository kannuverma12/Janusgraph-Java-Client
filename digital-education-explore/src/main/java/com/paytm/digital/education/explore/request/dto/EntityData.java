package com.paytm.digital.education.explore.request.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.EducationEntity;
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

    @JsonProperty("pid")
    private Long pid;

    @JsonProperty("mid")
    private Long mid;

    public static class Constants {

        public static final String MID = "mid";

        public static final String PID = "pid";

        public static final String COLLEGE_PREDICTOR_ID = "college_predictor_id";

    }


}
