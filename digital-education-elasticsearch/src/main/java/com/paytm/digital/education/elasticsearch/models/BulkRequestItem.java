package com.paytm.digital.education.elasticsearch.models;

import com.paytm.digital.education.enums.es.BulkRequestOperation;
import lombok.Data;

@Data
public class BulkRequestItem {

    private String id;

    private String index;

    private String type;

    private BulkRequestOperation operation;

    private Object source;

}
