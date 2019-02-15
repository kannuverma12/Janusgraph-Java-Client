package com.paytm.digital.education.elasticsearch.models;

import lombok.Data;

@Data
public class SearchField {

    private String name;

    private String path;

    private float boost;

}
