package com.paytm.digital.education.elasticsearch.query;

import static com.paytm.digital.education.elasticsearch.constants.ESConstants.AGGREGATION_QUERY_SIZE;
import static com.paytm.digital.education.elasticsearch.constants.ESConstants.DEFAULT_TERMS_AGGREGATION_BUCKETS_SIZE;
import static com.paytm.digital.education.elasticsearch.constants.ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS;
import static com.paytm.digital.education.elasticsearch.constants.ESConstants.INCLUDE_AGGREGATION_SUFFIX;
import static com.paytm.digital.education.elasticsearch.constants.ESConstants.MAX_AGGREGATION_SUFFIX;
import static com.paytm.digital.education.elasticsearch.constants.ESConstants.MIN_AGGREGATION_SUFFIX;

import com.paytm.digital.education.elasticsearch.enums.AggregationType;
import com.paytm.digital.education.elasticsearch.enums.BucketAggregationSortParms;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.FilterField;
import com.paytm.digital.education.elasticsearch.models.Operator;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.BucketSort;
import com.paytm.digital.education.elasticsearch.models.SortField;
import com.paytm.digital.education.elasticsearch.query.helper.PathWiseMultiMatchQueryMapBuilder;
import com.paytm.digital.education.elasticsearch.utils.DataSortUtil;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
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
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Collection;


@Service
public class AggregationQueryBuilderService {

    /**
     * Creates a map containing term level query with operator for every nested path and parent document
     */
    private Map<String, Map<Pair<String, Operator>, List<QueryBuilder>>> fillFilterFieldsQueryMap(
            FilterField[] filterFields) {

        String fieldName;
        String path;
        Map<String, Map<Pair<String, Operator>, List<QueryBuilder>>> filterQueries =
                new HashMap<>();

        for (FilterField field : filterFields) {

            if (StringUtils.isNotBlank(field.getName()) && field.getValues() != null) {
                path = StringUtils.isBlank(field.getPath())
                        ? DUMMY_PATH_FOR_OUTERMOST_FIELDS
                        : field.getPath();
                fieldName =
                        DUMMY_PATH_FOR_OUTERMOST_FIELDS.equals(path) ? field.getName()
                                : path + '.' + field.getName();

                List<QueryBuilder> filterQueryList = new ArrayList<>();
                if (field.getType().equals(FilterQueryType.TERMS)) {
                    QueryBuilder filterQuery = QueryBuilders.termsQuery(fieldName,
                            (Collection<?>) field.getValues());
                    filterQueryList.add(filterQuery);
                } else if (field.getType() == FilterQueryType.RANGE) {
                    // TODO: validation in BL2
                    List<List<Double>> values = (List<List<Double>>) field.getValues();
                    for (List<Double> value : values) {
                        QueryBuilder filterQuery = QueryBuilders.rangeQuery(fieldName)
                                .from(value.get(0))
                                .to(value.get(1));
                        filterQueryList.add(filterQuery);
                    }
                }
                if (!CollectionUtils.isEmpty(filterQueryList)) {
                    if (!filterQueries.containsKey(path)) {
                        filterQueries.put(path, new HashMap<>());
                    }
                    filterQueries.get(path)
                            .put(new Pair<>(fieldName, field.getOperator()), filterQueryList);
                }
            }

        }
        return filterQueries;
    }

    private QueryBuilder addSearchQueryListIntoRequest(Map<String, QueryBuilder> searchQueries) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (!CollectionUtils.isEmpty(searchQueries)) {
            searchQueries.forEach((path, multiMatchQuery) -> {

                if (DUMMY_PATH_FOR_OUTERMOST_FIELDS.equals(path)) {
                    boolQuery.must(multiMatchQuery);
                } else {
                    boolQuery.filter(
                            QueryBuilders.nestedQuery(path, multiMatchQuery, ScoreMode.None));
                }
            });
        }

        return boolQuery;
    }

    private void addTermLevelQueryIntoBool(List<QueryBuilder> filterQueries,
            BoolQueryBuilder boolQuery, Operator operator) {
        for (QueryBuilder query : filterQueries) {
            if (Operator.AND.equals(operator)) {
                boolQuery.filter(query);
            } else {
                boolQuery.should(query).minimumShouldMatch(1);
            }
        }
    }

    /**
     * Add query for filtering documents before aggregation
     */
    private QueryBuilder addAggregationFilterQuery(String aggFieldName,
            Map<String, Map<Pair<String, Operator>, List<QueryBuilder>>> filterQueries,
            AggregationType type) {

        BoolQueryBuilder filterAggregationQuery = QueryBuilders.boolQuery();

        if (filterQueries != null) {

            filterQueries.forEach((path, fieldQueryMap) -> {

                if (!CollectionUtils.isEmpty(fieldQueryMap)) {

                    if (DUMMY_PATH_FOR_OUTERMOST_FIELDS.equals(path)) {
                        fieldQueryMap.forEach((fieldAndOperator, filterQueryList) -> {
                            if (type.equals(AggregationType.TOP_HITS)
                                    || !aggFieldName.equals(fieldAndOperator.getKey())) {
                                addTermLevelQueryIntoBool(filterQueryList, filterAggregationQuery,
                                        fieldAndOperator.getValue());
                            }
                        });
                    } else {
                        BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                        fieldQueryMap.forEach((fieldAndOperator, filterQueryList) -> {
                            if (type.equals(AggregationType.TOP_HITS)
                                    || !aggFieldName.equals(fieldAndOperator.getKey())) {
                                addTermLevelQueryIntoBool(filterQueryList, nestedBoolQuery,
                                        fieldAndOperator.getValue());
                            }
                        });
                        filterAggregationQuery.filter(QueryBuilders
                                .nestedQuery(path, nestedBoolQuery, ScoreMode.None));
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
            Map<String, Map<Pair<String, Operator>, List<QueryBuilder>>> filterQueries,
            int bucketDocCount) {

        String path;
        String fieldName;

        for (AggregateField field : aggregateFields) {

            if (StringUtils.isNotBlank(field.getName())) {
                path = StringUtils.isBlank(field.getPath())
                        ? DUMMY_PATH_FOR_OUTERMOST_FIELDS
                        : field.getPath();
                fieldName =
                        DUMMY_PATH_FOR_OUTERMOST_FIELDS.equals(path) ? field.getName()
                                : path + '.' + field.getName();
                QueryBuilder filterQuery =
                        addAggregationFilterQuery(fieldName, filterQueries, field.getType());
                AggregationBuilder filterAggregation =
                        AggregationBuilders.filter(fieldName, filterQuery);

                if (field.getType().equals(AggregationType.TERMS)) {
                    TermsAggregationBuilder termsAggregation =
                            AggregationBuilders.terms(fieldName).field(fieldName);
                    /**
                     * We are doing sorting of nested aggregation on count at application level.
                     */
                    if (DUMMY_PATH_FOR_OUTERMOST_FIELDS.equals(path)
                            || (field.getBucketsOrder() != null
                            && !field.getBucketsOrder().getKey()
                            .equals(BucketAggregationSortParms.COUNT))) {
                        termsAggregation.order(getBucketAggregationOrder(field.getBucketsOrder()));
                    }
                    termsAggregation.size(DEFAULT_TERMS_AGGREGATION_BUCKETS_SIZE);

                    TermsAggregationBuilder termsAggregationInclude = null;
                    if (Objects.nonNull(field.getValues())) {
                        IncludeExclude excludeValues = new IncludeExclude(null, field.getValues());
                        termsAggregation.includeExclude(excludeValues);
                        termsAggregationInclude =
                                AggregationBuilders
                                        .terms(fieldName + INCLUDE_AGGREGATION_SUFFIX);
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
                    if (DUMMY_PATH_FOR_OUTERMOST_FIELDS.equals(path)) {
                        filterAggregation.subAggregation(termsAggregation);
                        if (Objects.nonNull(termsAggregationInclude)) {
                            filterAggregation.subAggregation(termsAggregationInclude);
                        }
                    } else {
                        termsAggregation
                                .subAggregation(AggregationBuilders.reverseNested(fieldName));

                        NestedAggregationBuilder nestedAggregation =
                                new NestedAggregationBuilder(fieldName, path);
                        nestedAggregation.subAggregation(termsAggregation);
                        if (Objects.nonNull(termsAggregationInclude)) {
                            termsAggregationInclude
                                    .subAggregation(AggregationBuilders.reverseNested(
                                            fieldName + INCLUDE_AGGREGATION_SUFFIX));
                            nestedAggregation.subAggregation(termsAggregationInclude);
                        }
                        filterAggregation.subAggregation(nestedAggregation);
                    }
                    source.aggregation(filterAggregation);

                } else if (field.getType().equals(AggregationType.MINMAX)) {
                    /**
                     * Using suffix because two sibling aggregations cannot have save name
                     */
                    MinAggregationBuilder minAggregation = AggregationBuilders
                            .min(fieldName + MIN_AGGREGATION_SUFFIX)
                            .field(fieldName);
                    MaxAggregationBuilder maxAggregation = AggregationBuilders
                            .max(fieldName + MAX_AGGREGATION_SUFFIX)
                            .field(fieldName);

                    if (DUMMY_PATH_FOR_OUTERMOST_FIELDS.equals(path)) {
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
                } else if (field.getType().equals(AggregationType.TOP_HITS)) {
                    TopHitsAggregationBuilder topHitsAggregation =
                            AggregationBuilders.topHits(fieldName);
                    topHitsAggregation.size(bucketDocCount);
                    topHitsAggregation.fetchSource(true);
                    if (field.getSortFields() != null) {
                        for (SortField sortField : field.getSortFields()) {
                            topHitsAggregation.sort(DataSortUtil.buildSort(sortField, null));
                        }
                    }
                    TermsAggregationBuilder termsAggregation = AggregationBuilders.terms(fieldName);
                    termsAggregation.field(fieldName);
                    termsAggregation.size(DEFAULT_TERMS_AGGREGATION_BUCKETS_SIZE);
                    termsAggregation.subAggregation(topHitsAggregation);
                    if (DUMMY_PATH_FOR_OUTERMOST_FIELDS.equals(path)) {
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
        Map<String, Map<Pair<String, Operator>, List<QueryBuilder>>> filterQueries = null;

        if (Objects.nonNull(request.getSearchFields()) && StringUtils
                .isNotBlank(request.getQueryTerm())) {
            searchQueries =
                    PathWiseMultiMatchQueryMapBuilder.getQueryMap(request.getSearchFields(),
                            request.getAnalyzer(), request.getQueryTerm());
        }

        if (Objects.nonNull(request.getFilterFields())) {
            filterQueries = fillFilterFieldsQueryMap(request.getFilterFields());
        }

        SearchSourceBuilder source = new SearchSourceBuilder();
        source.size(AGGREGATION_QUERY_SIZE);
        source.query(addSearchQueryListIntoRequest(searchQueries));

        if (Objects.nonNull(request.getAggregateFields())) {
            addAggregationsIntoRequest(request.getAggregateFields(), source, filterQueries,
                    request.getLimit());
        }

        return new SearchRequest(request.getIndex()).source(source);
    }
}

