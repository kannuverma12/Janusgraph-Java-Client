package com.paytm.digital.education.elasticsearch.models;

import lombok.Data;

@Data
public class IndexObject {

    private String id;

    private String index;

    private String type;

    private Object source;

}
