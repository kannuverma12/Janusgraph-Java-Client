package com.paytm.digital.education.search.service.impl;

import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.service.ElasticSearchService;
import com.paytm.digital.education.search.service.ISearchService;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;


@Service
@AllArgsConstructor
public class ExploreSearchService implements ISearchService {

    private ElasticSearchService elasticSearchService;

    @Override public ElasticResponse search(ElasticRequest elasticRequest, Class type)
            throws IOException, TimeoutException {
        return elasticSearchService.executeSearch(elasticRequest, type);
    }
}
