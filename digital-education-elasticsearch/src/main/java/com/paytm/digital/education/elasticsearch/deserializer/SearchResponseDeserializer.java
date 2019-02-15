package com.paytm.digital.education.elasticsearch.deserializer;

import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.utils.JsonUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class SearchResponseDeserializer<T> {

    public List<T> formatResponse(SearchResponse esResponse, Class<T> type,
            ElasticResponse<T> response) {

        response.setSearchQueryTime(esResponse.getTook().getMicros());

        List<T> documents = new ArrayList<T>();
        SearchHit[] searchHits = esResponse.getHits().getHits();

        for (SearchHit searchHit : searchHits) {
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            T object = JsonUtils.convertValue(sourceAsMap, type);
            documents.add(object);
        }

        return documents;

    }
}
