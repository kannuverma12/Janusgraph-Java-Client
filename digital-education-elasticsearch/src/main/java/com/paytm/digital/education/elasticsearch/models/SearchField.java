package com.paytm.digital.education.elasticsearch.models;

import lombok.Data;

import static com.paytm.digital.education.elasticsearch.constants.ESConstants.DEFAULT_BOOST;

@Data
public class SearchField {

    private String name;

    private String path;

    private float boost = DEFAULT_BOOST;

}
