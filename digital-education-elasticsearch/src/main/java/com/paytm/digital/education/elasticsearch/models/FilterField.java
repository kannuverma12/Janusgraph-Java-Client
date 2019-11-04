package com.paytm.digital.education.elasticsearch.models;

import com.paytm.digital.education.enums.es.FilterQueryType;
import lombok.Data;

@Data
public class FilterField {

    private String name;

    private String path;

    private FilterQueryType type;

    private Object values;

    private Operator operator = Operator.AND;

}
