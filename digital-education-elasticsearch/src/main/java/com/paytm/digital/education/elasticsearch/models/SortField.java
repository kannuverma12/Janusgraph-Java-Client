package com.paytm.digital.education.elasticsearch.models;

import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import lombok.Data;

@Data
public class SortField {

    private String    name;

    private String    path;

    private DataSortOrder order;

}
