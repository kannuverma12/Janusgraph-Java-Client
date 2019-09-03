package com.paytm.digital.education.explore.thirdparty.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogProduct {

    @JsonProperty("name")
    private String name;

    @JsonProperty("variants")
    private List<Variant> variants;

}
