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
public class SocialLink implements Serializable {

    private static final long serialVersionUID = -5558975548960996067L;

    @Field("social_link_name")
    @JsonProperty("social_link_name")
    private String socialLinkName;

    @Field("social_link_url")
    @JsonProperty("social_link_url")
    private String socialLinkUrl;
}
