package com.paytm.digital.education.explore.thirdparty.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InnerVariant {

    @JsonProperty("course")
    private String course;

    @JsonProperty("group2")
    private String group2;

    @JsonProperty("products")
    private List<Product> products;

}