package com.paytm.digital.education.explore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TopicDto {

    @JsonProperty("topic_name")
    private String name;

}
