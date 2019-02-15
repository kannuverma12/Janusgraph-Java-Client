package com.paytm.digital.education.search.service;

import com.paytm.digital.education.elasticsearch.models.ElasticRequest;

import java.util.List;

public interface ISearchService {
    public <T> List<T> search(ElasticRequest elasticRequest, Class<T> type);
}
