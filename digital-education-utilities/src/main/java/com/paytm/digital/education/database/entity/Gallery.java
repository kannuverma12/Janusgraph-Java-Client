package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Gallery implements Serializable {
    private static final long serialVersionUID = 4994193724740640832L;

    @Field("images")
    private Map<String, List<String>> images;

    @Field("videos")
    private Map<String, List<String>> videos;

    @Field("logo")
    @JsonProperty("logo")
    private String logo;

}
