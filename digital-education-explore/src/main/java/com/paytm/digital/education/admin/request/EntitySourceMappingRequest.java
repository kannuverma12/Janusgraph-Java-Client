package com.paytm.digital.education.admin.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.database.entity.PaytmSourceData;
import com.paytm.digital.education.enums.EducationEntity;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntitySourceMappingRequest {

    @NotNull
    @JsonProperty("entity")
    private EducationEntity educationEntity;

    @NotEmpty
    @JsonProperty("data")
    private List<EntitySourceMappingData> entitySourceMappingData;
}
