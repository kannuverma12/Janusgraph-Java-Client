package com.paytm.digital.education.coaching.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Unit {

    @Field("index")
    @JsonProperty("index")
    private int index;

    @Field("unit_name")
    @JsonProperty("unit_name")
    private String name;

    @Field("topic")
    @JsonProperty("topic")
    private List<Topic> topics;

}
