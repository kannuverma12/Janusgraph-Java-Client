package com.paytm.digital.education.elasticsearch.models;

import lombok.Data;

@Data
public class CrossField extends SearchQueryType {

    private float tieBreaker = 0.0f;

}
