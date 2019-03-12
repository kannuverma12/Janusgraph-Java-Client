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
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.common.util.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.IncludeExclude;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.MinAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHitsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

            if (StringUtils.isNotBlank(field.getName())) {
                path = StringUtils.isBlank(field.getPath())
                        ? ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS
                        : field.getPath();
                fieldName =
                        path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS) ? field.getName()
                                : path + '.' + field.getName();

                if (!searchQueries.containsKey(path)) {
                    MultiMatchQueryBuilder multiMatchQuery =
                            QueryBuilders.multiMatchQuery(queryTerm);

                    if (StringUtils.isNotBlank(analyzer)) {
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

            if (StringUtils.isNotBlank(field.getName()) && field.getValues() != null) {
                path = StringUtils.isBlank(field.getPath())
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
                    List<Double> values = (List<Double>) field.getValues();
                    filterQuery = QueryBuilders.rangeQuery(fieldName)
                            .from(values.get(0))
                            .to(values.get(1));
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
                    boolQuery.must(multiMatchQuery);
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
            Map<String, Map<String, QueryBuilder>> filterQueries, AggregationType type) {

        BoolQueryBuilder filterAggregationQuery = QueryBuilders.boolQuery();

        if (filterQueries != null) {

            filterQueries.forEach((path, fieldQueryMap) -> {

                if (fieldQueryMap != null) {

                    if (path == ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS) {
                        fieldQueryMap.forEach((filterFieldName, filterQuery) -> {
                            if (type == AggregationType.TOP_HITS
                                    || !aggFieldName.equals(filterFieldName)) {
                                filterAggregationQuery.filter(filterQuery);
                            }
                        });
                    } else {
                        BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                        fieldQueryMap.forEach((filterFieldName, filterQuery) -> {
                            if (type == AggregationType.TOP_HITS
                                    || !aggFieldName.equals(filterFieldName)) {
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
            Map<String, Map<String, QueryBuilder>> filterQueries, int bucketDocCount) {

        String path;
        String fieldName;

        for (AggregateField field : aggregateFields) {

            if (StringUtils.isNotBlank(field.getName())) {
                path = StringUtils.isBlank(field.getPath())
                        ? ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS
                        : field.getPath();
                fieldName =
                        path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS) ? field.getName()
                                : path + '.' + field.getName();
                QueryBuilder filterQuery =
                        addAggregationFilterQuery(fieldName, filterQueries, field.getType());
                AggregationBuilder filterAggregation =
                        AggregationBuilders.filter(fieldName, filterQuery);

                if (field.getType() == AggregationType.TERMS) {
                    TermsAggregationBuilder termsAggregation =
                            AggregationBuilders.terms(fieldName).field(fieldName);
                    termsAggregation.order(getBucketAggregationOrder(field.getBucketsOrder()));
                    termsAggregation.size(ESConstants.DEFAULT_TERMS_AGGREGATION_BUCKETS_SIZE);

                    TermsAggregationBuilder termsAggregationInclude = null;
                    if (!CollectionUtils.isEmpty(field.getValues())) {
                        IncludeExclude excludeValues = new IncludeExclude(null, field.getValues());
                        termsAggregation.includeExclude(excludeValues);
                        termsAggregationInclude =
                                AggregationBuilders
                                        .terms(fieldName + ESConstants.INCLUDE_AGGREGATION_SUFFIX);
                        termsAggregationInclude.field(fieldName);
                        IncludeExclude includeValues = new IncludeExclude(field.getValues(), null);
                        termsAggregationInclude.includeExclude(includeValues);
                        termsAggregationInclude.field(fieldName);
                        termsAggregationInclude
                                .order(getBucketAggregationOrder(field.getBucketsOrder()));
                    }
                    /**
                     * Adding reverse nested in order to get count of parent documents in case of
                     * nested aggregations
                     */
                    if (path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS)) {
                        filterAggregation.subAggregation(termsAggregation);
                        if (termsAggregationInclude != null) {
                            filterAggregation.subAggregation(termsAggregationInclude);
                        }
                    } else {
                        termsAggregation
                                .subAggregation(AggregationBuilders.reverseNested(fieldName));

                        NestedAggregationBuilder nestedAggregation =
                                new NestedAggregationBuilder(fieldName, path);
                        nestedAggregation.subAggregation(termsAggregation);
                        if (termsAggregationInclude != null) {
                            termsAggregationInclude
                                    .subAggregation(AggregationBuilders.reverseNested(
                                            fieldName + ESConstants.INCLUDE_AGGREGATION_SUFFIX));
                            nestedAggregation.subAggregation(termsAggregationInclude);
                        }
                        filterAggregation.subAggregation(nestedAggregation);
                    }
                    source.aggregation(filterAggregation);

                } else if (field.getType() == AggregationType.MINMAX) {
                    /**
                     * Using suffix because two sibling aggregations cannot have save name
                     */
                    MinAggregationBuilder minAggregation = AggregationBuilders
                            .min(fieldName + ESConstants.MIN_AGGREGATION_SUFFIX)
                            .field(fieldName);
                    MaxAggregationBuilder maxAggregation = AggregationBuilders
                            .max(fieldName + ESConstants.MAX_AGGREGATION_SUFFIX)
                            .field(fieldName);

                    if (path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS)) {
                        filterAggregation.subAggregation(minAggregation);
                        filterAggregation.subAggregation(maxAggregation);
                    } else {
                        NestedAggregationBuilder nestedAggregation =
                                new NestedAggregationBuilder(fieldName, path);
                        nestedAggregation.subAggregation(minAggregation);
                        nestedAggregation.subAggregation(maxAggregation);
                        filterAggregation.subAggregation(nestedAggregation);
                    }
                    source.aggregation(filterAggregation);
                } else if (field.getType() == AggregationType.TOP_HITS) {
                    TopHitsAggregationBuilder topHitsAggregation =
                            AggregationBuilders.topHits(fieldName);
                    topHitsAggregation.size(bucketDocCount);
                    topHitsAggregation.fetchSource(true);

                    TermsAggregationBuilder termsAggregation = AggregationBuilders.terms(fieldName);
                    termsAggregation.field(fieldName);
                    termsAggregation.size(ESConstants.DEFAULT_TERMS_AGGREGATION_BUCKETS_SIZE);
                    termsAggregation.subAggregation(topHitsAggregation);
                    if (path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS)) {
                        filterAggregation.subAggregation(termsAggregation);
                    } else {
                        NestedAggregationBuilder nestedAggs =
                                AggregationBuilders.nested(fieldName, path);
                        nestedAggs.subAggregation(termsAggregation);
                        filterAggregation.subAggregation(nestedAggs);
                    }
                    source.aggregation(filterAggregation);
                }

            }
        }
    }

    public SearchRequest buildRequest(ElasticRequest request) {

        Map<String, QueryBuilder> searchQueries = null;
        Map<String, Map<String, QueryBuilder>> filterQueries = null;

        if (request.getSearchFields() != null && StringUtils.isNotBlank(request.getQueryTerm())) {
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
            addAggregationsIntoRequest(request.getAggregateFields(), source, filterQueries,
                    request.getLimit());
        }

        return new SearchRequest(request.getIndex()).source(source);
    }
}

