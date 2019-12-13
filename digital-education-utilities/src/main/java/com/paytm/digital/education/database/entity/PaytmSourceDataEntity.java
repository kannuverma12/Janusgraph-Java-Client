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

import java.io.Serializable;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document("paytm_source_data")
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@CompoundIndexes({
        @CompoundIndex(name = "entity_entity_id", def = "{'entity' : 1, 'entity_id': 1} ",unique = true)
    })
public class PaytmSourceDataEntity implements Serializable {

    private static final long serialVersionUID = -8592325086959190899L;

    @Id
    @Field("_id")
    private String id;

    @Field("entity_id")
    @JsonProperty("entity_id")
    private Long entityId;

    @Field("entity")
    @JsonProperty("entity")
    private EducationEntity educationEntity;

    @Field("source")
    private EntitySourceType source;

    @Field("is_active")
    private boolean isActive;

    @Field("exam_data")
    @JsonProperty("exam_data")
    private Exam examData;

    @Field("institute_data")
    @JsonProperty("institute_data")
    private Institute instituteData;

    @Field("school_data")
    @JsonProperty("school_data")
    private School schoolData;

    @Field("course_data")
    @JsonProperty("course_data")
    private Course courseData;

}
