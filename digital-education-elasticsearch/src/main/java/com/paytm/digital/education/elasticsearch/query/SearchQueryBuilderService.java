package com.paytm.digital.education.elasticsearch.query;

import com.paytm.digital.education.elasticsearch.constants.ESConstants;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.SortField;
import com.paytm.digital.education.elasticsearch.models.Operator;
import com.paytm.digital.education.elasticsearch.models.FilterField;
import com.paytm.digital.education.elasticsearch.query.helper.PathWiseMultiMatchQueryMapBuilder;
import com.paytm.digital.education.elasticsearch.utils.DataSortUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;

@Service
public class SearchQueryBuilderService {

    private QueryBuilder addQueryMapsIntoRequest(Map<String, QueryBuilder> searchQueries,
            Map<String, QueryBuilder> filterQueries) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (!CollectionUtils.isEmpty(searchQueries)) {
            searchQueries.forEach((path, multiMatchQuery) -> {
                if (ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS.equals(path)) {
                    boolQuery.must(multiMatchQuery);
                } else {
                    boolQuery.must(
                            QueryBuilders.nestedQuery(path, multiMatchQuery, ScoreMode.Avg));
                }
            });
        }

        if (!CollectionUtils.isEmpty(filterQueries)) {
            filterQueries.forEach((operatorPerPath, filterQuery) -> {
                if (ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS.equals(operatorPerPath)) {
                    boolQuery.filter(filterQuery);
                } else {
                    boolQuery.filter(QueryBuilders
                            .nestedQuery(operatorPerPath, filterQuery, ScoreMode.None));
                }
            });
        }
        return boolQuery;
    }

    private void addSortFieldsIntoRequest(ElasticRequest request, SearchSourceBuilder source,
            Map<String, QueryBuilder> filterQueries) {
        for (SortField sortField : request.getSortFields()) {
            if (Objects.nonNull(sortField.getName())) {
                source.sort(DataSortUtil.buildSort(sortField, filterQueries));
            }
        }
        /**
         * Default sorting order of search results
         */
        source.sort(ESConstants.RELAVANCE_SCORE, SortOrder.DESC);
    }

    private void addQueryToMap(String path, Operator operator, Map<String, QueryBuilder> queryMap,
            QueryBuilder query) {
        if (!queryMap.containsKey(path)) {
            queryMap.put(path, QueryBuilders.boolQuery());
        }
        if (Operator.AND.equals(operator)) {
            ((BoolQueryBuilder) queryMap.get(path)).filter(query);
        } else {
            ((BoolQueryBuilder) queryMap.get(path)).should(query).minimumShouldMatch(1);
        }
    }

    /**
     * Creates a map containing term level query for every nested path and parent document
     */
    private Map<String, QueryBuilder> fillFilterFieldsQueryMap(
            FilterField[] filterFields) {

        String fieldName;
        String path;
        Map<String, QueryBuilder> filterQueries = new HashMap<>();

        for (FilterField field : filterFields) {

            if (Objects.nonNull(field.getName()) && Objects.nonNull(field.getValues())) {
                path = StringUtils.isBlank(field.getPath())
                        ? ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS
                        : field.getPath();
                fieldName =
                        ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS.equals(path) ? field.getName()
                                : path + '.' + field.getName();

                if (field.getType().equals(FilterQueryType.TERMS)) {
                    QueryBuilder filterQuery = QueryBuilders.termsQuery(fieldName,
                            (Collection<?>) field.getValues());
                    addQueryToMap(path, field.getOperator(), filterQueries, filterQuery);
                } else if (field.getType().equals(FilterQueryType.RANGE)) {
                    List<List<Double>> values = (List<List<Double>>) field.getValues();
                    for (List<Double> value : values) {
                        QueryBuilder filterQuery = QueryBuilders.rangeQuery(fieldName)
                                .from(value.get(0))
                                .to(value.get(1));
                        addQueryToMap(path, field.getOperator(), filterQueries, filterQuery);
                    }
                } else {
                    // Keep a default query or throw exception?
                }
            }
        }
        return filterQueries;
    }

    public SearchRequest buildRequest(ElasticRequest request) {

        Map<String, QueryBuilder> searchQueries = null;
        Map<String, QueryBuilder> filterQueries = null;

        if (Objects.nonNull(request.getSearchFields()) && StringUtils
                .isNotBlank(request.getQueryTerm())) {
            searchQueries = PathWiseMultiMatchQueryMapBuilder.getQueryMap(request.getSearchFields(),
                    request.getAnalyzer(), request.getQueryTerm());
        }

        if (Objects.nonNull(request.getFilterFields())) {
            filterQueries = fillFilterFieldsQueryMap(request.getFilterFields());
        }

        int offset = Objects.nonNull(request.getOffSet())
                ? request.getOffSet() : ESConstants.DEFAULT_OFFSET;
        int limit = Objects.nonNull(request.getLimit())
                ? request.getLimit() : ESConstants.DEFAULT_LIMIT;

        SearchSourceBuilder source = new SearchSourceBuilder();
        source.from(offset);
        source.size(limit);
        source.query(addQueryMapsIntoRequest(searchQueries, filterQueries));

        if (Objects.nonNull(request.getSortFields())) {
            addSortFieldsIntoRequest(request, source, filterQueries);
        }

        return new SearchRequest(request.getIndex()).source(source);
    }
}
