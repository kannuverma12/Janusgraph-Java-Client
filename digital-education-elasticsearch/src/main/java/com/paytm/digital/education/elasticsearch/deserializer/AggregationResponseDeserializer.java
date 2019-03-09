package com.paytm.digital.education.elasticsearch.deserializer;

import com.paytm.digital.education.elasticsearch.constants.ESConstants;
import com.paytm.digital.education.elasticsearch.enums.AggregationType;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.AggregationResponse;
import com.paytm.digital.education.elasticsearch.models.Bucket;
import com.paytm.digital.education.elasticsearch.models.BucketAggregationResponse;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.MetricAggregationResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.util.CollectionUtils;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedReverseNested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class AggregationResponseDeserializer {

    private List<Bucket> getBucketsFromTermsAggregation(Terms termsAggregation, String path,
            String aggregationName) {

        List<Bucket> buckets = new ArrayList<>();
        String key;
        long count;
        for (Terms.Bucket esRespBucket : termsAggregation.getBuckets()) {
            key = esRespBucket.getKeyAsString();
            /**
             * For nested fields use count containing in reverse nested aggregation
             */
            if (path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS)) {
                count = esRespBucket.getDocCount();
            } else {
                ParsedReverseNested childAggregation =
                        esRespBucket.getAggregations().get(aggregationName);
                count = childAggregation.getDocCount();
            }

            buckets.add(new Bucket(key, count));
        }
        return buckets;
    }

    private AggregationResponse getTermsFromFilterAggregationResponse(Filter filterAggregation,
            String aggregationName, String path, boolean hasIncludeAggregation) {

        List<Bucket> buckets = new ArrayList<>();
        Terms termsAggregation;
        Terms termsIncludeAggregation = null;
        if (path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS)) {
            termsAggregation = filterAggregation.getAggregations().get(aggregationName);
            if (hasIncludeAggregation) {
                termsIncludeAggregation = filterAggregation.getAggregations()
                        .get(aggregationName + ESConstants.INCLUDE_AGGREGATION_SUFFIX);
            }
        } else {
            Nested nestedAgg = filterAggregation.getAggregations().get(aggregationName);
            termsAggregation = nestedAgg.getAggregations().get(aggregationName);
            if (hasIncludeAggregation) {
                termsIncludeAggregation = nestedAgg.getAggregations()
                        .get(aggregationName + ESConstants.INCLUDE_AGGREGATION_SUFFIX);
            }
        }

        if (hasIncludeAggregation) {
            buckets.addAll(
                    getBucketsFromTermsAggregation(termsIncludeAggregation, path,
                            aggregationName + ESConstants.INCLUDE_AGGREGATION_SUFFIX));
        }
        buckets.addAll(getBucketsFromTermsAggregation(termsAggregation, path, aggregationName));
        BucketAggregationResponse aggregationResponse = new BucketAggregationResponse();
        aggregationResponse.setBuckets(buckets);

        return aggregationResponse;
    }

    private AggregationResponse getBucketsFromMetricAggregationResponse(Filter filterAggregation,
            String aggregationName, String path) {

        MetricAggregationResponse aggregationResponse = new MetricAggregationResponse();
        Min minAggregation;
        Max maxAggregation;

        if (path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS)) {
            minAggregation = filterAggregation.getAggregations()
                    .get(aggregationName + ESConstants.MIN_AGGREGATION_SUFFIX);
            maxAggregation = filterAggregation.getAggregations()
                    .get(aggregationName + ESConstants.MAX_AGGREGATION_SUFFIX);
        } else {
            Nested nestedAgg = filterAggregation.getAggregations().get(aggregationName);
            minAggregation = nestedAgg.getAggregations()
                    .get(aggregationName + ESConstants.MIN_AGGREGATION_SUFFIX);
            maxAggregation = nestedAgg.getAggregations()
                    .get(aggregationName + ESConstants.MAX_AGGREGATION_SUFFIX);
        }

        String key = aggregationName;
        double minValue = minAggregation.getValue();
        double maxValue = maxAggregation.getValue();

        aggregationResponse.setKey(key);
        aggregationResponse.setMaxValue(maxValue);
        aggregationResponse.setMinValue(minValue);

        return aggregationResponse;
    }

    public Map<String, AggregationResponse> formatResponse(SearchResponse esResponse,
            ElasticRequest request) {

        Map<String, AggregationResponse> responseMap =
                new HashMap<String, AggregationResponse>();

        if (request.getAggregateFields() != null) {
            String path;
            String fieldName;
            String aggregationName;

            for (AggregateField field : request.getAggregateFields()) {
                path = StringUtils.isEmpty(field.getPath())
                        ? ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS
                        : field.getPath();
                aggregationName =
                        path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS) ? field.getName()
                                : path + '.' + field.getName();
                fieldName = field.getName();
                AggregationResponse aggResponse = null;
                Filter filterAggregation = esResponse.getAggregations().get(aggregationName);

                if (field.getType() == AggregationType.TERMS) {
                    boolean hasIncludeaggregation = !CollectionUtils.isEmpty(field.getValues());
                    aggResponse =
                            getTermsFromFilterAggregationResponse(filterAggregation,
                                    aggregationName, path, hasIncludeaggregation);
                } else if (field.getType() == AggregationType.MINMAX) {
                    aggResponse = getBucketsFromMetricAggregationResponse(filterAggregation,
                            aggregationName, path);
                }

                if (aggResponse != null) {
                    responseMap.put(fieldName, aggResponse);
                }
            }
        }

        return responseMap;
    }
}
