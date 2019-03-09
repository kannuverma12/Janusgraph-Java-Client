package com.paytm.digital.education.elasticsearch.models;

import javafx.util.Pair;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class TopHitsAggregationResponse<T> implements AggregationResponse {

    Map<Pair<String, Float>, List<T>> documentsPerEntity;

}
