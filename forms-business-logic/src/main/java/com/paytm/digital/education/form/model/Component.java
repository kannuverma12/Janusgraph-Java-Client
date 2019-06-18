package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Component {

    @JsonProperty("key")
    private String key;

    @JsonProperty("defaultValue")
    private String defaultValue;
}