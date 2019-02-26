package com.paytm.digital.education.search.service;

import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public interface ISearchService {
    public ElasticResponse search(ElasticRequest elasticRequest, Class type) throws IOException, TimeoutException;
}
