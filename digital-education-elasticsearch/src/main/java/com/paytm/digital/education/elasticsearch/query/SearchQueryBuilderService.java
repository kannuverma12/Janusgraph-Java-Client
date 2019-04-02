package com.paytm.digital.education.elasticsearch.query;

import com.paytm.digital.education.elasticsearch.constants.ESConstants;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.FilterField;
import com.paytm.digital.education.elasticsearch.models.SearchField;
import com.paytm.digital.education.elasticsearch.models.SortField;
import com.paytm.digital.education.elasticsearch.utils.DataSortUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.NestedSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import java.time.temporal.ValueRange;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchQueryBuilderService {

    private QueryBuilder addQueryMapsIntoRequest(Map<String, QueryBuilder> searchQueries,
            Map<String, QueryBuilder> filterQueries) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (searchQueries != null) {
            searchQueries.forEach((path, multiMatchQuery) -> {
                if (path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS)) {
                    boolQuery.must(multiMatchQuery);
                } else {
                    boolQuery.must(
                            QueryBuilders.nestedQuery(path, multiMatchQuery, ScoreMode.Avg));
                }
            });
        }

        if (filterQueries != null) {
            filterQueries.forEach((path, filterQuery) -> {
                if (path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS)) {
                    boolQuery.filter(filterQuery);
                } else {
                    boolQuery.filter(QueryBuilders.nestedQuery(path, filterQuery, ScoreMode.None));
                }
            });
        }
        return boolQuery;
    }

    private void addSortFieldsIntoRequest(ElasticRequest request, SearchSourceBuilder source,
            Map<String, QueryBuilder> filterQueries) {
        for (SortField sortField : request.getSortFields()) {
            if (sortField.getName() != null) {
                source.sort(DataSortUtil.buildSort(sortField, filterQueries));
            }
        }
        /**
         * Default sorting order of search results
         */
        source.sort(ESConstants.RELAVANCE_SCORE, SortOrder.DESC);
    }

    /**
     * Creates a map containing multiMatch query for every nested path and parent document
     */
    private Map<String, QueryBuilder> fillSearchFieldsQueryMap(SearchField[] searchFields,
            String queryTerm, String analyzer) {

        String path;
        String fieldName;
        float boost;
        Map<String, QueryBuilder> searchQueries = new HashMap<String, QueryBuilder>();

        for (SearchField field : searchFields) {
            if (field.getName() != null) {
                path = StringUtils.isBlank(field.getPath())
                        ? ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS
                        : field.getPath();
                fieldName =
                        path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS) ? field.getName()
                                : path + '.' + field.getName();
                boost = field.getBoost() == 0.0 ? ESConstants.DEFAULT_BOOST : field.getBoost();

                if (!searchQueries.containsKey(path)) {
                    MultiMatchQueryBuilder multiMatchQuery =
                            QueryBuilders.multiMatchQuery(queryTerm);
                    if (analyzer != null) {
                        multiMatchQuery.analyzer(analyzer);
                    }
                    multiMatchQuery.type(Type.CROSS_FIELDS);
                    searchQueries.put(path, multiMatchQuery);
                }

                ((MultiMatchQueryBuilder) searchQueries.get(path)).field(fieldName,
                        boost);
            }
        }

        return searchQueries;
    }

    /**
     * Creates a map containing term level query for every nested path and parent document
     */
    private Map<String, QueryBuilder> fillFilterFieldsQueryMap(FilterField[] filterFields) {

        String fieldName;
        String path;
        Map<String, QueryBuilder> filterQueries = new HashMap<String, QueryBuilder>();

        for (FilterField field : filterFields) {

            if (field.getName() != null && field.getValues() != null) {

                path = StringUtils.isBlank(field.getPath())
                        ? ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS
                        : field.getPath();
                fieldName =
                        path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS) ? field.getName()
                                : path + '.' + field.getName();
                QueryBuilder filtetQuery = null;

                if (field.getType() == FilterQueryType.TERMS) {
                    filtetQuery = QueryBuilders.termsQuery(fieldName,
                            (Collection<?>) field.getValues());
                } else if (field.getType() == FilterQueryType.RANGE) {
                    List<Double> values = (List<Double>) field.getValues();
                    filtetQuery = QueryBuilders.rangeQuery(fieldName)
                            .from(values.get(0))
                            .to(values.get(1));
                } else {
                    // Keep a default query or throw exception?
                }

                if (!filterQueries.containsKey(path)) {
                    filterQueries.put(path, QueryBuilders.boolQuery());
                }

                ((BoolQueryBuilder) filterQueries.get(path)).filter(filtetQuery);
            }
        }
        return filterQueries;
    }

    public SearchRequest buildRequest(ElasticRequest request) {

        Map<String, QueryBuilder> searchQueries = null;
        Map<String, QueryBuilder> filterQueries = null;

        if (request.getSearchFields() != null && StringUtils.isNotBlank(request.getQueryTerm())) {
            searchQueries = fillSearchFieldsQueryMap(request.getSearchFields(),
                    request.getQueryTerm(), request.getAnalyzer());
        }

        if (request.getFilterFields() != null) {
            filterQueries = fillFilterFieldsQueryMap(request.getFilterFields());
        }

        int offset =
                (request.getOffSet() != null) ? request.getOffSet() : ESConstants.DEFAULT_OFFSET;
        int limit =
                (request.getLimit() != null) ? request.getLimit() : ESConstants.DEFAULT_LIMIT;

        SearchSourceBuilder source = new SearchSourceBuilder();
        source.from(offset);
        source.size(limit);
        source.query(addQueryMapsIntoRequest(searchQueries, filterQueries));

        if (request.getSortFields() != null) {
            addSortFieldsIntoRequest(request, source, filterQueries);
        }


        return new SearchRequest(request.getIndex()).source(source);
    }
}
