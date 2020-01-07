package com.paytm.digital.education.explore.database.ingestion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Counselling implements Serializable {

    private static final long serialVersionUID = 51591934474256644L;
    @Field("id")
    @JsonProperty("id")
    private int id;

    @Field("name")
    @JsonProperty("name")
    private String name;
}
