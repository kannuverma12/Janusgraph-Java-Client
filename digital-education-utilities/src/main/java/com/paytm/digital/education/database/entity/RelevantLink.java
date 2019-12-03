package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelevantLink implements Serializable {
    @Field("relevant_link_type")
    @JsonProperty("relevant_link_type")
    private String relevantLinkType;

    @Field("relevant_link_url")
    @JsonProperty("relevant_link_url")
    private String relevantLinkUrl;
}
