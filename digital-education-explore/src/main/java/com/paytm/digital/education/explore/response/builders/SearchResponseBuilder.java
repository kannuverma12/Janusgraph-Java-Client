package com.paytm.digital.education.explore.response.builders;

import com.paytm.digital.education.elasticsearch.enums.AggregationType;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.AggregationResponse;
import com.paytm.digital.education.elasticsearch.models.BucketAggregationResponse;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.FilterField;
import com.paytm.digital.education.elasticsearch.models.MetricAggregationResponse;
import com.paytm.digital.education.explore.response.dto.search.FilterBucket;
import com.paytm.digital.education.explore.response.dto.search.FilterData;
import com.paytm.digital.education.explore.response.dto.search.RangeFilterData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.response.dto.search.TermFilterData;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SearchResponseBuilder {

    public <T> void populateSearchFilters(SearchResponse searchResponse,
            ElasticResponse<T> elasticResponse, ElasticRequest elasticRequest,
            Map<String, Map<String, Object>> propertyMap) {
        Map<String, AggregationResponse> aggregationResponse =
                elasticResponse.getAggregationResponse();
        List<FilterData> filters = new ArrayList<>();
        if (!CollectionUtils.isEmpty(aggregationResponse)) {
            Map<String, FilterField> filterFieldMap = elasticRequest.getFilterFieldDataMap();
            for (AggregateField aggField : elasticRequest.getAggregateFields()) {
                String fieldName = aggField.getName();
                if (aggregationResponse.containsKey(fieldName)) {
                    FilterData filter = null;
                    if (aggField.getType() == AggregationType.TERMS) {
                        BucketAggregationResponse bucketAggResponse =
                                (BucketAggregationResponse) aggregationResponse.get(fieldName);
                        if (!CollectionUtils.isEmpty(bucketAggResponse.getBuckets())) {
                            List<FilterBucket> filterBuckets = new ArrayList<>();
                            bucketAggResponse.getBuckets().forEach(bucket -> {
                                String keyName;
                                if (!CollectionUtils.isEmpty(propertyMap)
                                        && propertyMap.containsKey(fieldName) && propertyMap
                                                .get(fieldName).containsKey(bucket.getKey())) {
                                    keyName = propertyMap.get(fieldName).get(bucket.getKey())
                                            .toString();
                                } else {
                                    keyName = bucket.getKey();
                                }
                                FilterBucket filterBucket = FilterBucket.builder()
                                        .value(bucket.getKey())
                                        .displayName(keyName)
                                        .docCount(bucket.getDocCount())
                                        .isSelected(
                                                checkIfRequestedFilter(
                                                        filterFieldMap.get(fieldName),
                                                        bucket.getKey()))
                                        .build();

                                filterBuckets.add(filterBucket);
                            });
                            TermFilterData termFilter = new TermFilterData();
                            termFilter.setName(fieldName);
                            termFilter.setDisplayName(fieldName.toUpperCase());
                            termFilter.setBuckets(filterBuckets);
                            filters.add(termFilter);
                        }
                    } else if (aggField.getType() == AggregationType.MINMAX) {
                        RangeFilterData rangeFilter = new RangeFilterData();
                        MetricAggregationResponse metricAggResponse =
                                (MetricAggregationResponse) aggregationResponse.get(fieldName);

                        if (!Double.isInfinite(metricAggResponse.getMinValue())
                                && !Double.isInfinite(metricAggResponse.getMaxValue())) {
                            rangeFilter.setName(fieldName);
                            rangeFilter.setDisplayName(fieldName.toUpperCase());
                            rangeFilter.setMinValue(metricAggResponse.getMinValue());
                            rangeFilter.setMaxValue(metricAggResponse.getMaxValue());
                            Pair<Double, Double> selectionValue = getSelectedRange(rangeFilter,
                                    filterFieldMap.get(fieldName));
                            if (selectionValue != null) {
                                rangeFilter.setMinSelected(selectionValue.getKey());
                                rangeFilter.setMaxSelected(selectionValue.getValue());
                            }
                            filters.add(rangeFilter);
                        }
                    }
                }
            }
        }
        searchResponse.setFilters(filters);
    }

    private boolean checkIfRequestedFilter(FilterField filterField,
            Object filterValue) {
        if (filterField != null) {
            if (filterField.getValues() instanceof Collection) {
                List<Object> values = (List) filterField.getValues();
                if (values.contains(filterValue)) {
                    return true;
                }
            } else if (filterField.getValues().equals(filterValue)) {
                return true;
            }

        }
        return false;
    }


    private Pair<Double, Double> getSelectedRange(RangeFilterData rangeFilter,
            FilterField filterField) {
        if (filterField != null) {
            List<Object> values = (List<Object>) filterField.getValues();
            return new Pair<>(Double.parseDouble("" + values.get(0)),
                    Double.parseDouble("" + values.get(1)));
        }
        return null;
    }
}
