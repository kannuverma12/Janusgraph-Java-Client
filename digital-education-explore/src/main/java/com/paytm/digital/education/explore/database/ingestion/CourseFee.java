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
public class CourseFee  implements Serializable {

    private static final long serialVersionUID = -1753614543628299731L;
    @JsonProperty("fee")
    @Field("fee")
    private Integer fee;

    @JsonProperty("caste_group")
    @Field("caste_group")
    private String casteGroup;
}
