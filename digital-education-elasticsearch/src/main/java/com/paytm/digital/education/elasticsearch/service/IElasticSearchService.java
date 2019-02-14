package com.paytm.digital.education.elasticsearch.service;

import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;

public interface IElasticSearchService {

    public ElasticResponse executeSearch(ElasticRequest request);
    
}
