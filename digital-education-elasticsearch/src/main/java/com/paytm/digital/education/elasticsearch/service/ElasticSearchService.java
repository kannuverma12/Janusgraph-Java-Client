package com.paytm.digital.education.elasticsearch.service;

import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.BulkRequestItem;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public interface ElasticSearchService {

    public  <T> ElasticResponse<T> executeSearch(ElasticRequest request, Class<T> type)
            throws IOException, TimeoutException;

    public Map<String, String> executeInBulk(Map<String, BulkRequestItem> documents) throws IOException;

}
