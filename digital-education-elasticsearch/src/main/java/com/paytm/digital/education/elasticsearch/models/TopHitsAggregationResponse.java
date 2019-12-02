package com.paytm.digital.education.elasticsearch.models;

import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

@Data
public class TopHitsAggregationResponse<T> implements AggregationResponse {

    Map<Pair<String, Float>, List<T>> documentsPerEntity;

}
