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

    @JsonProperty("college_predictor_id")
    @Field("college_predictor_id")
    private Long collegePredictorId;

    @Field("form_id")
    @JsonProperty("form_id")
    private String formId;

    @Field("webFormUriPrefix")
    private String webFormUriPrefix;

}
