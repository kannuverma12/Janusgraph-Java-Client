package com.paytm.digital.education.coaching.consumer.model.response.search.builder;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.consumer.model.response.search.FilterBucket;
import com.paytm.digital.education.coaching.consumer.model.response.search.FilterData;
import com.paytm.digital.education.coaching.consumer.model.response.search.MultipleRangeData;
import com.paytm.digital.education.coaching.consumer.model.response.search.RangeFilterData;
import com.paytm.digital.education.coaching.consumer.model.response.search.RangeFilterValue;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResponse;
import com.paytm.digital.education.coaching.consumer.model.response.search.TermFilterData;
import com.paytm.digital.education.elasticsearch.enums.AggregationType;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.AggregationResponse;
import com.paytm.digital.education.elasticsearch.models.BucketAggregationResponse;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.FilterField;
import com.paytm.digital.education.elasticsearch.models.MetricAggregationResponse;
import com.paytm.digital.education.utility.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class CoachingSearchResponseBuilder {

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
                        MetricAggregationResponse metricAggResponse =
                                (MetricAggregationResponse) aggregationResponse.get(fieldName);
                        if (!Double.isInfinite(metricAggResponse.getMinValue())
                                && !Double.isInfinite(metricAggResponse.getMaxValue())) {
                            if (CoachingConstants.Search.FEES.equals(aggField.getName())) {
                                MultipleRangeData multipleRangeData =
                                        getMultipleRangeData(fieldName, propertyMap,
                                                metricAggResponse.getMaxValue(),
                                                metricAggResponse.getMinValue());
                                if (filterFieldMap.containsKey(fieldName)) {
                                    setSelectedRangeValues(multipleRangeData,
                                            filterFieldMap.get(fieldName));
                                }
                                filters.add(multipleRangeData);
                            } else {
                                RangeFilterData rangeFilter = new RangeFilterData();
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
        }
        searchResponse.setFilters(filters);
    }

    private MultipleRangeData getMultipleRangeData(String fieldName,
            Map<String, Map<String, Object>> propertyMap, double maxValue, double minValue) {
        MultipleRangeData feeFilterData = new MultipleRangeData();
        feeFilterData.setName(fieldName);
        feeFilterData.setDisplayName(CommonUtil.getDisplayName(propertyMap, fieldName, fieldName));
        List<RangeFilterValue> values = new ArrayList<>();
        if (propertyMap.containsKey(CoachingConstants.Search.FEES) && propertyMap
                .get(CoachingConstants.Search.FEES).containsKey(CoachingConstants.Search.DATA)) {
            List<Map<String, Object>> rangeData =
                    (List<Map<String, Object>>) propertyMap.get(CoachingConstants.Search.FEES)
                            .get(CoachingConstants.Search.DATA);
            for (Map<String, Object> range : rangeData) {
                List<Double> keys = (List<Double>) range.get(CoachingConstants.Search.KEY);
                if (maxValue > keys.get(0) && minValue <= keys.get(1)) {
                    RangeFilterValue filterValue = new RangeFilterValue();
                    List<Double> minMaxValues = new ArrayList<>();
                    minMaxValues.add(keys.get(0));
                    minMaxValues.add(keys.get(1));
                    filterValue.setValues(minMaxValues);
                    filterValue.setDisplayName(
                            range.get(CoachingConstants.Search.DISPLAY_NAME).toString());
                    values.add(filterValue);
                }
            }
        }
        feeFilterData.setValues(values);
        return feeFilterData;
    }

    private void setSelectedRangeValues(MultipleRangeData rangeValuesData, FilterField filterData) {
        List<List<Object>> selectedValues = (List<List<Object>>) filterData.getValues();
        for (List<Object> value : selectedValues) {
            Double maxSelectedValue = new Double(((Integer) value.get(1)));
            for (RangeFilterValue rangeFilterValue : rangeValuesData.getValues()) {
                if (maxSelectedValue.equals(rangeFilterValue.getValues().get(1))) {
                    rangeFilterValue.setSelected(true);
                    break;
                }
            }
        }
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