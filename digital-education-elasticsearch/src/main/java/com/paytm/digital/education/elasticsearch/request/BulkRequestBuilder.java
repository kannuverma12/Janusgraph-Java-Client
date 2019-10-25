package com.paytm.digital.education.elasticsearch.request;

import com.paytm.digital.education.elasticsearch.models.BulkRequestItem;
import com.paytm.digital.education.elasticsearch.utils.JsonUtils;
import com.paytm.digital.education.enums.es.BulkRequestOperation;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BulkRequestBuilder {

    public BulkRequest build(Map<String, BulkRequestItem> documents) {
        BulkRequest bulkRequest = new BulkRequest();
        for (Map.Entry<String, BulkRequestItem> document : documents.entrySet()) {
            if (BulkRequestOperation.INDEX.equals(document.getValue().getOperation())) {
                String docJsonStr = JsonUtils.toJson(document.getValue().getSource());
                bulkRequest.add(new IndexRequest(document.getValue().getIndex())
                        .type(document.getValue().getType()).id(document.getKey())
                        .source(docJsonStr, XContentType.JSON));
            } else if (BulkRequestOperation.DELETE.equals(document.getValue().getOperation())) {
                bulkRequest.add(new DeleteRequest().index(document.getValue().getIndex())
                        .type(document.getValue().getType()).id(document.getKey()));
            }
        }
        return bulkRequest;
    }

}
