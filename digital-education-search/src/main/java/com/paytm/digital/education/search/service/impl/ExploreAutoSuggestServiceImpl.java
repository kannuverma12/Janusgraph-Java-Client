package com.paytm.digital.education.search.service.impl;

import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.service.ElasticSearchService;
import com.paytm.digital.education.search.service.AutoSuggestionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
@AllArgsConstructor
public class ExploreAutoSuggestServiceImpl implements AutoSuggestionService {

    private ElasticSearchService elasticSearchService;

    @Override
    public <T> ElasticResponse<T> suggest(ElasticRequest elasticRequest, Class<T> type)
            throws IOException, TimeoutException {
        ElasticResponse<T> elasticResponse = elasticSearchService.executeSearch(elasticRequest, type);
        return elasticResponse;
    }
}
