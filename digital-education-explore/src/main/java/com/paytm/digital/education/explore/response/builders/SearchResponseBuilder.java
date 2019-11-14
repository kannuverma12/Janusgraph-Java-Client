package com.paytm.digital.education.explore.response.builders;

import static com.paytm.digital.education.constant.ExploreConstants.DATA;
import static com.paytm.digital.education.constant.ExploreConstants.DISPLAY_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.DISTANCE_KILOMETERS;
import static com.paytm.digital.education.constant.ExploreConstants.FEES;
import static com.paytm.digital.education.constant.ExploreConstants.KEY;

import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.AggregationResponse;
import com.paytm.digital.education.elasticsearch.models.BucketAggregationResponse;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.FilterField;
import com.paytm.digital.education.elasticsearch.models.MetricAggregationResponse;
import com.paytm.digital.education.enums.es.AggregationType;
import com.paytm.digital.education.explore.response.dto.search.FilterBucket;
import com.paytm.digital.education.explore.response.dto.search.FilterData;
import com.paytm.digital.education.explore.response.dto.search.MultipleRangeData;
import com.paytm.digital.education.explore.response.dto.search.RangeFilterData;
import com.paytm.digital.education.explore.response.dto.search.RangeFilterValue;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.response.dto.search.TermFilterData;
import com.paytm.digital.education.explore.service.impl.DisplayNameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SearchResponseBuilder {

    private final DisplayNameService displayNameService;

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
                                        .displayName(displayNameService.getDisplayName(propertyMap,
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
                                    displayNameService.getDisplayName(propertyMap, fieldName,
                                            fieldName));
                            termFilter.setBuckets(filterBuckets);
                            filters.add(termFilter);
                        }
                    } else if (aggField.getType().equals(AggregationType.MINMAX)) {
                        MetricAggregationResponse metricAggResponse =
                                (MetricAggregationResponse) aggregationResponse.get(fieldName);
                        if (!Double.isInfinite(metricAggResponse.getMinValue())
                                && !Double.isInfinite(metricAggResponse.getMaxValue())) {
                            if (FEES.equals(aggField.getName())) {
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
                                rangeFilter.setDisplayName(displayNameService
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
                    } else if (aggField.getType().equals(AggregationType.GEO_DISTANCE)) {
                        MetricAggregationResponse metricAggResponse =
                                (MetricAggregationResponse) aggregationResponse.get(fieldName);
                        RangeFilterData rangeFilter = new RangeFilterData();
                        rangeFilter.setName(fieldName);
                        rangeFilter.setDisplayName(
                                displayNameService.getDisplayName(propertyMap, fieldName, fieldName));
                        rangeFilter.setMaxValue(metricAggResponse.getMaxValue());
                        rangeFilter.setMinValue(metricAggResponse.getMinValue());
                        rangeFilter.setUnit(DISTANCE_KILOMETERS);
                        rangeFilter.setDisableMinSlider(true);

                        if (Objects.nonNull(filterFieldMap.get(fieldName)) && Objects
                                .nonNull(filterFieldMap.get(fieldName).getValues())) {
                            List<Object> values =
                                    (List<Object>) filterFieldMap.get(fieldName).getValues();
                            if (!CollectionUtils.isEmpty(values)) {
                                List<List<Integer>> selectionValues = new ArrayList<>();
                                selectionValues.add(Arrays.asList(0, Integer.parseInt(
                                        (String) values.get(0))));
                                rangeFilter.setSelectedValues(selectionValues);
                            }
                        }
                        filters.add(rangeFilter);
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
        feeFilterData.setDisplayName(displayNameService.getDisplayName(propertyMap, fieldName, fieldName));
        List<RangeFilterValue> values = new ArrayList<>();
        if (propertyMap.containsKey(FEES) && propertyMap.get(FEES).containsKey(DATA)) {
            List<Map<String, Object>> rangeData =
                    (List<Map<String, Object>>) propertyMap.get(FEES).get(DATA);
            for (Map<String, Object> range : rangeData) {
                List<Double> keys = (List<Double>) range.get(KEY);
                if (Double.compare(maxValue, keys.get(0)) > 0
                        && Double.compare(minValue, keys.get(1)) <= 0) {
                    RangeFilterValue filterValue = new RangeFilterValue();
                    List<Double> minMaxValues = new ArrayList<>();
                    minMaxValues.add(keys.get(0));
                    minMaxValues.add(keys.get(1));
                    filterValue.setValues(minMaxValues);
                    filterValue.setDisplayName(range.get(DISPLAY_NAME).toString());
                    values.add(filterValue);
                }
            }
        }
        feeFilterData.setValues(values);
        return feeFilterData;
    }

    private void setSelectedRangeValues(MultipleRangeData rangeValuesData, FilterField filterData) {
        if (Objects.nonNull(filterData) && Objects.nonNull(filterData.getValues())) {
            List<List<Integer>> selectedValues = (List<List<Integer>>) filterData.getValues();
            int minVal = selectedValues.get(0).get(0);
            int maxVal = selectedValues.get(0).get(1);
            for (int i = 1; i < selectedValues.size(); i++) {
                if (selectedValues.get(i).get(0) < minVal) {
                    minVal = selectedValues.get(i).get(0);
                }
                if (selectedValues.get(i).get(1) > maxVal) {
                    maxVal = selectedValues.get(i).get(1);
                }
            }

            for (RangeFilterValue rangeFilterValue : rangeValuesData.getValues()) {
                List<Double> rangeData = rangeFilterValue.getValues();
                if (Double.compare(minVal, rangeData.get(0)) <= 0
                        && Double.compare(rangeData.get(1), maxVal) <= 0) {
                    rangeFilterValue.setSelected(true);
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
