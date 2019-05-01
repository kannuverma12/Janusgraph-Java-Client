package com.paytm.digital.education.elasticsearch.query.helper;

import com.paytm.digital.education.elasticsearch.models.CrossField;
import com.paytm.digital.education.elasticsearch.models.SearchField;
import com.paytm.digital.education.elasticsearch.models.SearchQueryType;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import static com.paytm.digital.education.elasticsearch.constants.ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS;

@UtilityClass
public class PathWiseMultiMatchQueryMapBuilder {

    /**
     * Creates a map containing multiMatch query for every nested path and parent document
     */
    public Map<String, QueryBuilder> getQueryMap(SearchField[] searchFields,
            String analyzer, String queryTerm, SearchQueryType searchQueryType) {

        String fieldName;
        String path;
        Float boost;
        Map<String, QueryBuilder> searchQueries = new HashMap<String, QueryBuilder>();

        for (SearchField field : searchFields) {

            if (StringUtils.isNotBlank(field.getName())) {
                path = StringUtils.isBlank(field.getPath())
                        ? DUMMY_PATH_FOR_OUTERMOST_FIELDS
                        : field.getPath();
                fieldName =
                        path.equals(DUMMY_PATH_FOR_OUTERMOST_FIELDS) ? field.getName()
                                : path + '.' + field.getName();

                if (!searchQueries.containsKey(path)) {
                    MultiMatchQueryBuilder multiMatchQuery =
                            QueryBuilders.multiMatchQuery(queryTerm);

                    if (StringUtils.isNotBlank(analyzer)) {
                        multiMatchQuery.analyzer(analyzer);
                    }
                    if (Objects.nonNull(searchQueryType) && searchQueryType instanceof CrossField) {
                        multiMatchQuery.type(MultiMatchQueryBuilder.Type.CROSS_FIELDS);
                        multiMatchQuery.tieBreaker(((CrossField) searchQueryType).getTieBreaker());
                    }
                    searchQueries.put(path, multiMatchQuery);
                }

                ((MultiMatchQueryBuilder) searchQueries.get(path))
                        .field(fieldName, field.getBoost());
            }
        }
        return searchQueries;
    }
}
