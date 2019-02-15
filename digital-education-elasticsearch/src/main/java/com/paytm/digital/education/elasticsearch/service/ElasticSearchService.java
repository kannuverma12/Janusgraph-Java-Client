package com.paytm.digital.education.elasticsearch.service;

import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface ElasticSearchService<T> {

    public  ElasticResponse<T> executeSearch(ElasticRequest request, Class<T> type)
            throws IOException, TimeoutException;

}
