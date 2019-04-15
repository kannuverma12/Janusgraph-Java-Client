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
import com.paytm.digital.education.explore.utility.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collection;

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
                    if (aggField.getType().equals(AggregationType.TERMS)) {
                        BucketAggregationResponse bucketAggResponse =
                                (BucketAggregationResponse) aggregationResponse.get(fieldName);
                        if (!CollectionUtils.isEmpty(bucketAggResponse.getBuckets())) {
                            List<FilterBucket> filterBuckets = new ArrayList<>();
                            bucketAggResponse.getBuckets().forEach(bucket -> {
                                FilterBucket filterBucket = FilterBucket.builder()
                                        .value(bucket.getKey())
                                        .displayName(CommonUtil.getDisplayName(propertyMap,
                                                fieldName,
                                                bucket.getKey()))
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
                            termFilter.setDisplayName(
                                    CommonUtil.getDisplayName(propertyMap, fieldName,
                                            fieldName));
                            termFilter.setBuckets(filterBuckets);
                            filters.add(termFilter);
                        }
                    } else if (aggField.getType().equals(AggregationType.MINMAX)) {
                        RangeFilterData rangeFilter = new RangeFilterData();
                        MetricAggregationResponse metricAggResponse =
                                (MetricAggregationResponse) aggregationResponse.get(fieldName);

                        if (!Double.isInfinite(metricAggResponse.getMinValue())
                                && !Double.isInfinite(metricAggResponse.getMaxValue())) {
                            rangeFilter.setName(fieldName);
                            rangeFilter.setDisplayName(CommonUtil
                                    .getDisplayName(propertyMap, fieldName, fieldName));
                            rangeFilter.setMinValue(metricAggResponse.getMinValue());
                            rangeFilter.setMaxValue(metricAggResponse.getMaxValue());
                            if (Objects.nonNull(filterFieldMap.get(fieldName)) && Objects
                                    .nonNull(filterFieldMap.get(fieldName).getValues())) {
                                List<List<Integer>> selectionValue =
                                        (List<List<Integer>>) filterFieldMap.get(fieldName)
                                                .getValues();
                                rangeFilter.setSelectedValues(selectionValue);
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
}
