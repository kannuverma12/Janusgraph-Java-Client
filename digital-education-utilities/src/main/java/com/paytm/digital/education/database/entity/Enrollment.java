package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.ClassType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class Enrollment {

    @Field("class_from")
    @JsonProperty("class_from")
    private ClassType classFrom;

    @Field("class_to")
    @JsonProperty("class_to")
    private ClassType classTo;

    @Field("enrollment")
    @JsonProperty("enrollment")
    private Integer enrollment;
}
