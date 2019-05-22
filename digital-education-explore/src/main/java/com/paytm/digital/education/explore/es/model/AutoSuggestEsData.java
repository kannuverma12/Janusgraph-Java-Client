package com.paytm.digital.education.explore.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.EducationEntity;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutoSuggestEsData {

    @JsonProperty("entity_id")
    private long entityId;

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("logo")
    private String logo;

    @JsonProperty("official_address")
    private OfficialAddress officialAddress;

    @JsonProperty("entity_type")
    private EducationEntity entityType;

    @JsonProperty("names")
    private List<String> names;
}
