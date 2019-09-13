package com.paytm.digital.education.admin.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.Client;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Validated
public class SectionOrderRequest {

    @NotBlank
    @JsonProperty("page")
    private String page;

    @NotBlank
    @JsonProperty("entity")
    private String entity;

    @JsonProperty("client")
    private Client client;

    @NotEmpty
    @JsonProperty("section-order")
    private List<String> sectionOrder;

}
