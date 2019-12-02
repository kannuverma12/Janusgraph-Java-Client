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
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document("paytm_source_data")
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaytmSourceData {

    @Id
    @Field("_id")
    @JsonIgnore
    private String id;

    @Field("entity_id")
    @JsonProperty("entity_id")
    private Long entityId;

    @Field("entity")
    @JsonProperty("entity")
    private EducationEntity educationEntity;

    @Field("source")
    private EntitySourceType source;

    @Field("data")
    private Map<String, Object> data;

}
