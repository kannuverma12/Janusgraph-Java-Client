package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.ClassType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class Enrollment implements Serializable {
    private static final long serialVersionUID = -8989428193349292886L;

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
