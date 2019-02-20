package com.paytm.digital.education.elasticsearch.deserializer;

import com.paytm.digital.education.elasticsearch.utils.JsonUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SearchResponseDeserializer {

    public <T> List<T> formatResponse(SearchResponse esResponse, Class<T> type) {

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
