package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.EntitySourceType;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaytmSourceData {

    @Id
    private String id;

    @JsonProperty("entity_id")
    private Long entityId;

    @JsonProperty("entity")
    private EducationEntity educationEntity;

    @JsonProperty("source")
    private EntitySourceType source;

    @JsonProperty("is_active")
    private boolean isActive;

    @JsonProperty("entity_data")
    private Map<String, Object> data;

}
