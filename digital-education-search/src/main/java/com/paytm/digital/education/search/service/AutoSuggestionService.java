package com.paytm.digital.education.search.service;

import com.paytm.digital.education.elasticsearch.models.ElasticRequest;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public interface AutoSuggestionService {

    public <T> List<T> suggest(ElasticRequest elasticRequest, Class<T> type)
            throws IOException, TimeoutException;
}
