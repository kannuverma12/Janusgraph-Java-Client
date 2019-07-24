package com.paytm.digital.education.explore.database.ingestion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseFee {

    @JsonProperty("fee")
    @Field("fee")
    private Integer fee;

    @JsonProperty("caste_group")
    @Field("caste_group")
    private String casteGroup;
}
