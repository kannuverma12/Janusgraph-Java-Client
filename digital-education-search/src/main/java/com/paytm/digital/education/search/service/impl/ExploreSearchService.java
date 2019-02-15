package com.paytm.digital.education.search.service.impl;

import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.search.service.ISearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ExploreSearchService implements ISearchService {

    @Override public <T> List<T> search(ElasticRequest elasticRequest, Class<T> type) {
        return null;
    }
}
