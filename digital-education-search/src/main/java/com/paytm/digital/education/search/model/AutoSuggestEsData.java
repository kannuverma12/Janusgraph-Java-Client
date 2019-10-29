package com.paytm.digital.education.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.enums.EducationEntity;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AutoSuggestEsData {

    private long            entityId;
    private String          officialName;
    private Long            cityId;
    private Long            stateId;
    private String          logo;
    private OfficialAddress officialAddress;
    private EducationEntity entityType;
    private List<String>    names;
}
