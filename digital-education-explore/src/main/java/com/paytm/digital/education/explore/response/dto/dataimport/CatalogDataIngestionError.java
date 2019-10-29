package com.paytm.digital.education.explore.response.dto.dataimport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.EducationEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CatalogDataIngestionError {

    @JsonProperty("entity_id")
    private Long entityId;

    @JsonProperty("error")
    private String errorMessage;

    @JsonProperty("data")
    private String data;

    @JsonProperty("entity")
    private EducationEntity entity;

}
