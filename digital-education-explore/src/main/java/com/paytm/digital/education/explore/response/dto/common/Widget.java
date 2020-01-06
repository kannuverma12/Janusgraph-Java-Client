package com.paytm.digital.education.explore.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Widget implements Serializable {

    private static final long serialVersionUID = -6567690793591957106L;

    @JsonProperty("entity")
    private String entity;

    @JsonProperty("label")
    private String label;

    @JsonProperty("data")
    private List<WidgetData> data;

}
