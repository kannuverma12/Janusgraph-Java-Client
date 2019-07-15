package com.paytm.digital.education.elasticsearch.request;

import com.paytm.digital.education.elasticsearch.models.IndexObject;
import com.paytm.digital.education.elasticsearch.utils.JsonUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BulkRequestBuilder {

    public BulkRequest build(Map<String, IndexObject> documents) {
        BulkRequest bulkRequest = new BulkRequest();
        for (Map.Entry<String, IndexObject> document : documents.entrySet()) {
            String docJsonStr = JsonUtils.toJson(document.getValue().getSource());
            bulkRequest.add(new IndexRequest(document.getValue().getIndex())
                    .type(document.getValue().getType()).id(document.getKey())
                    .source(docJsonStr, XContentType.JSON));
        }
        return bulkRequest;
    }
}
