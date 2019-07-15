package com.paytm.digital.education.elasticsearch.service;

import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.IndexObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public interface ElasticSearchService {

    public  <T> ElasticResponse<T> executeSearch(ElasticRequest request, Class<T> type)
            throws IOException, TimeoutException;

    public Map<String, String> ingest(Map<String, IndexObject> documents) throws IOException;

}
