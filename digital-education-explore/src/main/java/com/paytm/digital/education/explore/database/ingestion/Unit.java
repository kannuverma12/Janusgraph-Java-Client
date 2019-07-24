package com.paytm.digital.education.explore.database.ingestion;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Unit {

    @JsonProperty("index")
    @Field("index")
    private int index;

    @JsonProperty("unit_name")
    @Field("unit_name")
    private String name;
    
    @JsonProperty("topic")
    @Field("topic")
    private List<Topic> topics;
    
}
