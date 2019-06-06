package com.paytm.digital.education.form.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.form.model.CollegePredictor;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PredictorListResponse {

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("predictors")
    private List<CollegePredictor> collegePredictors;
}
