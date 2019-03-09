package com.paytm.digital.education.search.service;

import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface AutoSuggestionService {

    public <T> ElasticResponse<T> suggest(ElasticRequest elasticRequest, Class<T> type)
            throws IOException, TimeoutException;
}
