package com.paytm.digital.education.elasticsearch.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Bucket {

    @JsonProperty("key")
    private String key;

    @JsonProperty("count")
    private long   docCount;

}
