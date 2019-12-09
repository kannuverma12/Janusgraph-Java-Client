package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Attribute implements Serializable {

    private static final long serialVersionUID = 6202038971129007279L;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;
}
