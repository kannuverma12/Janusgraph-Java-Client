package com.paytm.digital.education.elasticsearch.service.impl;

import com.paytm.digital.education.elasticsearch.constants.ESConstants;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.query.SearchQueryBuilder;
import com.paytm.digital.education.elasticsearch.service.IElasticSearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import java.io.IOException;



public class ElasticSearchServiceImpl implements IElasticSearchService {

    private SearchQueryBuilder  searchQueryBuilder;

    @Qualifier(ESConstants.EDUCATION_ES_CLIENT)
    @Autowired
    private RestHighLevelClient esClient;

    @Override
    public ElasticResponse executeSearch(ElasticRequest request) {
        SearchRequest searchRequest = searchQueryBuilder.buildRequest(request);
        System.out.print(searchRequest.source().toString());
        try {
            SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }



}
