package com.paytm.digital.education.database.entity;

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

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document("entity_source_mapping")
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@CompoundIndexes({
        @CompoundIndex(name = "entity_entity_id", def = "{'entity' : 1, 'entity_id': 1} ",unique = true)
    })
public class EntitySourceMappingEntity {

    private static final long serialVersionUID = -8592325086959190L;

    @Id
    @Field("_id")
    private String id;

    @Field("entity_id")
    @JsonProperty("entity_id")
    private Long entityId;

    @Field("entity")
    @JsonProperty("entity")
    private EducationEntity educationEntity;

    @Field("entity_source")
    private EntitySourceType source;

    @Field("is_active")
    private boolean isActive;
}
