package com.paytm.digital.education.elasticsearch.service.impl;

import com.paytm.digital.education.elasticsearch.constants.ESConstants;
import com.paytm.digital.education.elasticsearch.deserializer.SearchResponseDeserializer;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.query.SearchQueryService;
import com.paytm.digital.education.elasticsearch.service.ElasticSearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
public class ElasticSearchServiceImpl<T> implements ElasticSearchService<T> {

    @Qualifier(ESConstants.EDUCATION_ES_CLIENT)
    @Autowired
    private RestHighLevelClient esClient;

    @Autowired
    SearchQueryService          searchQueryService;

    @Override
    public ElasticResponse<T> executeSearch(ElasticRequest request, Class<T> type)
            throws IOException, TimeoutException {

        ElasticResponse<T> response = new ElasticResponse<T>();

        if (request.isSearchRequest()) {

            SearchRequest searchRequest = searchQueryService.buildRequest(request);
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);


            if (searchResponse.isTimedOut()) {
                throw new TimeoutException();
            }

            SearchResponseDeserializer<T> searchResponseDes = new SearchResponseDeserializer<T>();

            long timeTaken = searchResponse.getTook().micros();
            List<T> documents = searchResponseDes.formatResponse(searchResponse, type, response);

            response.setDocuments(documents);
            response.setSearchQueryTime(timeTaken);

        }

        return response;
    }

}
