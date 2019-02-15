package com.paytm.digital.education.elasticsearch.models;

import lombok.Data;

import java.util.List;

@Data
public class ElasticResponse<T> {

    private long searchQueryTime;

    private List<T> documents;

}
