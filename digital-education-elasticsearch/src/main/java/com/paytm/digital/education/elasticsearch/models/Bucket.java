package com.paytm.digital.education.elasticsearch.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Bucket {

    private String key;

    private long docCount;

}
