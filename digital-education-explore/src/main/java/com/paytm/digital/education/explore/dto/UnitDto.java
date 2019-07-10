package com.paytm.digital.education.explore.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UnitDto {

    @JsonProperty("index")
    private int index;

    @JsonProperty("unit_name")
    private String name;
    
    @JsonProperty("topic")
    private List<TopicDto> topics;
    
}
