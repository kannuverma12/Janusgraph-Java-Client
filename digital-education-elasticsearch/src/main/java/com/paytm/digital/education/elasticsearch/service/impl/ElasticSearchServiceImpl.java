package com.paytm.digital.education.elasticsearch.service.impl;

import com.paytm.digital.education.elasticsearch.constants.ESConstants;
import com.paytm.digital.education.elasticsearch.deserializer.AggregationResponseDeserializer;
import com.paytm.digital.education.elasticsearch.deserializer.SearchResponseDeserializer;
import com.paytm.digital.education.elasticsearch.models.AggregationResponse;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.BulkRequestItem;
import com.paytm.digital.education.elasticsearch.query.AggregationQueryBuilderService;
import com.paytm.digital.education.elasticsearch.query.SearchQueryBuilderService;
import com.paytm.digital.education.elasticsearch.request.BulkRequestBuilder;
import com.paytm.digital.education.elasticsearch.service.ElasticSearchService;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Service
@AllArgsConstructor
public class ElasticSearchServiceImpl implements ElasticSearchService {

    private static Logger log = LoggerFactory.getLogger(ElasticSearchServiceImpl.class);

    @Qualifier(ESConstants.EDUCATION_ES_CLIENT)
    private RestHighLevelClient esClient;

    private SearchQueryBuilderService searchQueryService;

    private AggregationQueryBuilderService aggregationQueryService;

    private AggregationResponseDeserializer aggregationResponseDes;

    private SearchResponseDeserializer searchResponseDes;

    private BulkRequestBuilder bulkRequestBuilder;

    @Override
    public <T> ElasticResponse<T> executeSearch(ElasticRequest request, Class<T> type)
            throws IOException, TimeoutException {

        ElasticResponse<T> response = new ElasticResponse<T>();

        if (request.isSearchRequest()) {

            SearchRequest elasticSearchRequest = searchQueryService.buildRequest(request);
            log.debug("Elastic Search Request (Search) : {}",
                    elasticSearchRequest.source().toString());

            SearchResponse elasticSearchResponse =
                    esClient.search(elasticSearchRequest, RequestOptions.DEFAULT);
            log.debug("Elastic Search response (Search) : {}", elasticSearchResponse.toString());

            if (elasticSearchResponse.isTimedOut()) {
                throw new TimeoutException();
            }

            long totalDocumentsCount = elasticSearchResponse.getHits().totalHits;
            long timeTakenInElasticSearchQueryExecution = elasticSearchResponse.getTook().micros();
            /**
             * Deserialise ES response into list for document of type 'T' provided by the caller.
             */
            List<T> documents = searchResponseDes.formatResponse(elasticSearchResponse, type);
            response.setTotalSearchResultsCount(totalDocumentsCount);
            response.setDocuments(documents);
            response.setSearchQueryExecutionTime(timeTakenInElasticSearchQueryExecution);
        }

        if (request.isAggregationRequest()) {

            SearchRequest elasticAggregationRequest = aggregationQueryService.buildRequest(request);
            log.debug("Elastic Search Request (Aggregation) : {}",
                    elasticAggregationRequest.source().toString());

            SearchResponse elasticAggregationResponse =
                    esClient.search(elasticAggregationRequest, RequestOptions.DEFAULT);
            log.debug("Elastic Search response (Aggregation) : {}",
                    elasticAggregationResponse.toString());

            long timeTakenInElasticFilterQueryExecution =
                    elasticAggregationResponse.getTook().micros();

            if (elasticAggregationResponse.isTimedOut()) {
                throw new TimeoutException();
            }

            /**
             * Deserialise ES aggregation response into a generic response. A map of key(field name)
             * and value (Aggregation data)
             */
            Map<String, AggregationResponse> aggregationResponse =
                    aggregationResponseDes.formatResponse(elasticAggregationResponse, request,
                            type);
            response.setAggregationResponse(aggregationResponse);
            response.setAggregationQueryExecutionTime(timeTakenInElasticFilterQueryExecution);
        }

        return response;
    }

    @Override
    public Map<String, String> executeInBulk(Map<String, BulkRequestItem> documents) throws IOException {

        if (documents.size() > ESConstants.MAX_BULK_SIZE) {
            log.error("Bulk request too large");
            throw new BadRequestException(ErrorEnum.BULK_REQUEST_LIMIT_EXCEEDED,
                    ErrorEnum.BULK_REQUEST_LIMIT_EXCEEDED.getExternalMessage());
        }

        BulkRequest bulkRequest = bulkRequestBuilder.build(documents);

        BulkResponse bulkResponse = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        Map<String, String> response = new HashMap<>();
        if (bulkResponse.hasFailures()) {
            for (BulkItemResponse bulkItemResponse : bulkResponse.getItems()) {
                if (bulkItemResponse.isFailed()) {
                    response.put(bulkItemResponse.getId(), bulkItemResponse.getFailureMessage());
                }
            }
        }
        return response;
    }

}
