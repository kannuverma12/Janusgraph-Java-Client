package com.paytm.digital.education.elasticsearch.query;

import com.paytm.digital.education.elasticsearch.constants.ESConstants;
import com.paytm.digital.education.elasticsearch.enums.AggregationType;
import com.paytm.digital.education.elasticsearch.enums.BucketAggregationSortParms;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.BucketSort;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.FilterField;
import com.paytm.digital.education.elasticsearch.models.SearchField;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.temporal.ValueRange;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class AggregationQueryBuilderService {

    /**
     * Creates a map containing multiMatch query for every nested path and parent document
     */
    private Map<String, QueryBuilder> fillSearchFieldsQueryMap(SearchField[] searchFields,
            String analyzer, String queryTerm) {

        String fieldName;
        String path;
        Map<String, QueryBuilder> searchQueries = new HashMap<String, QueryBuilder>();

        for (SearchField field : searchFields) {

            if (!StringUtils.isEmpty(field.getName())) {
                path = StringUtils.isEmpty(field.getPath())
                        ? ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS
                        : field.getPath();
                fieldName =
                        path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS) ? field.getName()
                                : path + '.' + field.getName();

                if (!searchQueries.containsKey(path)) {
                    MultiMatchQueryBuilder multiMatchQuery =
                            QueryBuilders.multiMatchQuery(queryTerm);

                    if (!StringUtils.isEmpty(analyzer)) {
                        multiMatchQuery.analyzer(analyzer);
                    }

                    multiMatchQuery.type(Type.CROSS_FIELDS);
                    searchQueries.put(path, multiMatchQuery);
                }

                ((MultiMatchQueryBuilder) searchQueries.get(path)).field(fieldName);
            }
        }

        return searchQueries;
    }

    /**
     * Creates a map containing term level query for every nested path and parent document
     */
    private Map<String, Map<String, QueryBuilder>> fillFilterFieldsQueryMap(
            FilterField[] filterFields) {

        String fieldName;
        String path;
        Map<String, Map<String, QueryBuilder>> filterQueries =
                new HashMap<String, Map<String, QueryBuilder>>();

        for (FilterField field : filterFields) {

            if (!StringUtils.isEmpty(field.getName()) && field.getValues() != null) {
                path = StringUtils.isEmpty(field.getPath())
                        ? ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS
                        : field.getPath();
                fieldName =
                        path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS) ? field.getName()
                                : path + '.' + field.getName();
                QueryBuilder filterQuery = null;

                if (field.getType() == FilterQueryType.TERMS) {
                    filterQuery = QueryBuilders.termsQuery(fieldName,
                            (Collection<?>) field.getValues());
                } else if (field.getType() == FilterQueryType.RANGE) {
                    // TODO: validation in BL2
                    filterQuery = QueryBuilders.rangeQuery(fieldName)
                            .from(((ValueRange) field.getValues()).getMinimum())
                            .to(((ValueRange) field.getValues()).getMaximum());
                }

                if (!filterQueries.containsKey(path)) {
                    filterQueries.put(path, new HashMap<String, QueryBuilder>());
                }
                filterQueries.get(path).put(fieldName, filterQuery);
            }
        }

        return filterQueries;
    }

    private QueryBuilder addSearchQueryListIntoRequest(Map<String, QueryBuilder> searchQueries) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (searchQueries != null) {
            searchQueries.forEach((path, multiMatchQuery) -> {

                if (path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS)) {
                    boolQuery.filter(multiMatchQuery);
                } else {
                    boolQuery.filter(
                            QueryBuilders.nestedQuery(path, multiMatchQuery, ScoreMode.None));
                }
            });
        }

        return boolQuery;
    }

    /**
     * Add query for filtering documents before aggregation
     */
    private QueryBuilder addAggregationFilterQuery(String aggFieldName,
            Map<String, Map<String, QueryBuilder>> filterQueries) {

        BoolQueryBuilder filterAggregationQuery = QueryBuilders.boolQuery();

        if (filterQueries != null) {
            
            filterQueries.forEach((path, fieldQueryMap) -> {

                if (fieldQueryMap != null) {

                    if (path == ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS) {
                        fieldQueryMap.forEach((filterFieldName, filterQuery) -> {
                            if (!aggFieldName.equals(filterFieldName)) {
                                filterAggregationQuery.filter(filterQuery);
                            }
                        });
                    } else {
                        BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                        fieldQueryMap.forEach((filterFieldName, filterQuery) -> {
                            if (aggFieldName != filterFieldName) {
                                nestedBoolQuery.filter(filterQuery);
                            }
                        });
                        filterAggregationQuery
                                .filter(QueryBuilders.nestedQuery(path, nestedBoolQuery,
                                        ScoreMode.None));
                    }
                }
            });
        }
        return filterAggregationQuery;
    }

    private BucketOrder getBucketAggregationOrder(BucketSort order) {

        BucketOrder bucketsOrder;

        if (order.getKey() == BucketAggregationSortParms.KEY) {
            switch (order.getOrder()) {
                case ASC:
                    bucketsOrder = BucketOrder.key(true);
                    break;
                case DESC:
                default:
                    bucketsOrder = BucketOrder.key(false);
            }
        } else {
            switch (order.getOrder()) {
                case ASC:
                    bucketsOrder = BucketOrder.count(true);
                    break;
                case DESC:
                default:
                    bucketsOrder = BucketOrder.count(false);
            }
        }

        return bucketsOrder;
    }

    private void addAggregationsIntoRequest(AggregateField[] aggregateFields,
            SearchSourceBuilder source,
            Map<String, Map<String, QueryBuilder>> filterQueries) {

        String path;
        String fieldName;

        for (AggregateField field : aggregateFields) {

            if (!StringUtils.isEmpty(field.getName())) {
                path = StringUtils.isEmpty(field.getPath())
                        ? ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS
                        : field.getPath();
                fieldName =
                        path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS) ? field.getName()
                                : path + '.' + field.getName();
                QueryBuilder filterQuery = addAggregationFilterQuery(fieldName, filterQueries);
                AggregationBuilder filterAggregation =
                        AggregationBuilders.filter(fieldName, filterQuery);

                if (field.getType() == AggregationType.TERMS) {
                    TermsAggregationBuilder termsAggregation =
                            AggregationBuilders.terms(fieldName).field(fieldName);
                    termsAggregation.order(getBucketAggregationOrder(field.getBucketsOrder()));
                    termsAggregation.size(ESConstants.DEFAULT_TERMS_AGGREGATION_BUCKETS_SIZE);
                    /**
                     * Adding reverse nested in order to get count of parent documents in case of
                     * nested aggregations
                     */
                    if (!path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS)) {
                        termsAggregation
                                .subAggregation(AggregationBuilders.reverseNested(fieldName));
                    }

                    filterAggregation.subAggregation(termsAggregation);

                } else if (field.getType() == AggregationType.MINMAX) {
                    /**
                     * Using suffix because two sibling aggregations cannot have save name
                     */
                    filterAggregation
                            .subAggregation(
                                    AggregationBuilders
                                            .min(fieldName + ESConstants.MIN_AGGREGATION_SUFFIX)
                                            .field(fieldName));
                    filterAggregation
                            .subAggregation(
                                    AggregationBuilders
                                            .max(fieldName + ESConstants.MAX_AGGREGATION_SUFFIX)
                                            .field(fieldName));
                }

                if (path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS)) {
                    source.aggregation(filterAggregation);
                } else {
                    NestedAggregationBuilder nestedAggregation =
                            new NestedAggregationBuilder(fieldName, path);
                    nestedAggregation.subAggregation(filterAggregation);
                    source.aggregation(nestedAggregation);
                }
            }
        }
    }

    public SearchRequest buildRequest(ElasticRequest request) {

        Map<String, QueryBuilder> searchQueries = null;
        Map<String, Map<String, QueryBuilder>> filterQueries = null;

        if (request.getSearchFields() != null && !StringUtils.isEmpty(request.getQueryTerm())) {
            searchQueries = fillSearchFieldsQueryMap(request.getSearchFields(),
                    request.getAnalyzer(), request.getQueryTerm());
        }

        if (request.getFilterFields() != null) {
            filterQueries = fillFilterFieldsQueryMap(request.getFilterFields());
        }

        SearchSourceBuilder source = new SearchSourceBuilder();
        source.size(ESConstants.AGGREGATION_QUERY_SIZE);
        source.query(addSearchQueryListIntoRequest(searchQueries));

        if (request.getAggregateFields() != null) {
            addAggregationsIntoRequest(request.getAggregateFields(), source, filterQueries);
        }

        return new SearchRequest(request.getIndex()).source(source);
    }
}

