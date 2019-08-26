package com.paytm.digital.education.explore.response.dto.dataimport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.EducationEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
