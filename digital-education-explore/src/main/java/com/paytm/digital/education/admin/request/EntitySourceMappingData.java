package com.paytm.digital.education.admin.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.EntitySourceType;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntitySourceMappingData {

    @Id
    private String id;

    @JsonProperty("entity_id")
    private Long entityId;

    @JsonProperty("entity_source")
    private EntitySourceType source;

}
