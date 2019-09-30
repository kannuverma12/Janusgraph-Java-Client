package com.paytm.digital.education.elasticsearch.deserializer;

import com.paytm.digital.education.elasticsearch.constants.ESConstants;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.AggregationResponse;
import com.paytm.digital.education.elasticsearch.models.Bucket;
import com.paytm.digital.education.elasticsearch.models.BucketAggregationResponse;
import com.paytm.digital.education.elasticsearch.models.BucketSort;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.MetricAggregationResponse;
import com.paytm.digital.education.elasticsearch.models.TopHitsAggregationResponse;
import com.paytm.digital.education.elasticsearch.utils.BucketSortUtil;
import com.paytm.digital.education.elasticsearch.utils.JsonUtils;
import com.paytm.digital.education.enums.es.AggregationType;
import com.paytm.digital.education.enums.es.BucketAggregationSortParms;
import com.paytm.digital.education.enums.es.DataSortOrder;
import javafx.util.Pair;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.util.CollectionUtils;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedReverseNested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class AggregationResponseDeserializer {

    private List<Bucket> getBucketsFromTermsAggregation(Terms termsAggregation, String path,
            String aggregationName, BucketSort bucketsOrder) {

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
        if (!path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS) && bucketsOrder != null
                && bucketsOrder.getKey().equals(BucketAggregationSortParms.COUNT)) {
            if (bucketsOrder.getOrder().equals(DataSortOrder.ASC)) {
                Collections.sort(buckets, BucketSortUtil.ascendingCountSort);
            } else {
                Collections.sort(buckets, BucketSortUtil.descendingCountSort);
            }
        }
        return buckets;
    }

    private AggregationResponse getTermsFromFilterAggregationResponse(Filter filterAggregation,
            String aggregationName, String path, boolean hasIncludeAggregation,
            BucketSort bucketsOrder) {

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
                            aggregationName + ESConstants.INCLUDE_AGGREGATION_SUFFIX,
                            bucketsOrder));
        }
        buckets.addAll(getBucketsFromTermsAggregation(termsAggregation, path, aggregationName,
                bucketsOrder));
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

    private <T> List<T> getDocumentsFromBuckets(TopHits topHitsAggregtaion,
            Class<T> type) {
        List<T> documents = new ArrayList<>();
        SearchHit[] searchHits = topHitsAggregtaion.getHits().getHits();
        for (SearchHit searchHit : searchHits) {
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            T object = JsonUtils.convertValue(sourceAsMap, type);
            documents.add(object);
        }
        return documents;
    }

    private <T> TopHitsAggregationResponse<T> getDocumentsPerEntityFromTopHitsAggregationResponse(
            Filter filterAggregation, String aggregationName, String path, Class<T> type) {

        TopHitsAggregationResponse<T> aggregationResponse = new TopHitsAggregationResponse<>();

        Map<Pair<String, Float>, List<T>> documentsPerEntity = getEmptyPairListMap();

        Terms termsAggregation;

        if (path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS)) {
            termsAggregation = filterAggregation.getAggregations().get(aggregationName);
        } else {
            Nested nestedAggregation = filterAggregation.getAggregations().get(aggregationName);
            termsAggregation = nestedAggregation.getAggregations().get(aggregationName);
        }
        for (Terms.Bucket bucket : termsAggregation.getBuckets()) {
            TopHits topHitsAggregtaion = bucket.getAggregations().get(aggregationName);
            List<T> documentsScoreMap =
                    getDocumentsFromBuckets(topHitsAggregtaion, type);
            String entityName = bucket.getKeyAsString();
            Float maxScore = topHitsAggregtaion.getHits().getMaxScore();
            Pair<String, Float> key = new Pair<>(entityName, maxScore);
            documentsPerEntity.put(key, documentsScoreMap);
        }
        aggregationResponse.setDocumentsPerEntity(documentsPerEntity);
        return aggregationResponse;
    }


    public <T> Map<String, AggregationResponse> formatResponse(SearchResponse esResponse,
            ElasticRequest request, Class<T> type) {

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
                                    aggregationName, path, hasIncludeaggregation,
                                    field.getBucketsOrder());
                } else if (field.getType() == AggregationType.MINMAX) {
                    aggResponse = getBucketsFromMetricAggregationResponse(filterAggregation,
                            aggregationName, path);
                } else if (field.getType() == AggregationType.TOP_HITS) {
                    if (field.getChildTermsFieldName() != null) {
                        aggResponse = getDocumentsPerTopEntityFromTopHitsAggregationResponse(
                                filterAggregation, aggregationName, field.getChildTermsFieldName(),
                                path, type);
                    } else {
                        aggResponse = getDocumentsPerEntityFromTopHitsAggregationResponse(
                                filterAggregation, aggregationName, path, type);
                    }
                }

                if (aggResponse != null) {
                    responseMap.put(fieldName, aggResponse);
                }
            }
        }

        return responseMap;
    }

    private <T> AggregationResponse getDocumentsPerTopEntityFromTopHitsAggregationResponse(
            Filter filterAggregation, String aggregationName, String childAggregationName,
            String path,
            Class<T> type) {
        TopHitsAggregationResponse<T> aggregationResponse = new TopHitsAggregationResponse<>();

        Map<Pair<String, Float>, List<T>> documentsPerEntity = getEmptyPairListMap();

        Terms termsAggregation;

        if (path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS)) {
            termsAggregation = filterAggregation.getAggregations().get(aggregationName);
        } else {
            Nested nestedAggregation = filterAggregation.getAggregations().get(aggregationName);
            termsAggregation = nestedAggregation.getAggregations().get(aggregationName);
        }
        for (Terms.Bucket bucket : termsAggregation.getBuckets()) {
            Terms termsChildAggregtaion = bucket.getAggregations().get(childAggregationName);
            List<T> documentsScoreMap = new ArrayList<>();
            for (Terms.Bucket childBucket : termsChildAggregtaion.getBuckets()) {
                TopHits topHitsAggregation =
                        childBucket.getAggregations().get(aggregationName);
                documentsScoreMap.addAll(getDocumentsFromBuckets(topHitsAggregation, type));
                String entityName = bucket.getKeyAsString()
                        + ESConstants.KEY_SEPERATOR
                        + childBucket.getKeyAsString();
                Float maxScore = topHitsAggregation.getHits().getMaxScore();
                Pair<String, Float> key = new Pair<>(entityName, maxScore);
                documentsPerEntity.put(key, documentsScoreMap);
                aggregationResponse.setDocumentsPerEntity(documentsPerEntity);
            }
        }

        return aggregationResponse;
    }

    private <T> Map<Pair<String, Float>, List<T>> getEmptyPairListMap() {
        return new TreeMap<>((p1, p2) -> {
            if (p2.getValue() > p1.getValue()) {
                return 1;
            } else if (p2.getValue() < p1.getValue()) {
                return -1;
            }
            return 1;
        });
    }
}
