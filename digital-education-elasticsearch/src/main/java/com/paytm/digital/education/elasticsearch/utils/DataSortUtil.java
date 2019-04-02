package com.paytm.digital.education.elasticsearch.utils;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.NestedSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.CollectionUtils;
import com.paytm.digital.education.elasticsearch.constants.ESConstants;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.models.SortField;

public class DataSortUtil {

    private static SortBuilder<FieldSortBuilder> buildNestedSort(String fieldName, String path,
            DataSortOrder order, Map<String, QueryBuilder> filterQueries) {

        NestedSortBuilder nestedSort = new NestedSortBuilder(path);

        if (!CollectionUtils.isEmpty(filterQueries) && filterQueries.containsKey(path)) {
            nestedSort.setFilter(filterQueries.get(path));
        }

        SortBuilder<FieldSortBuilder> sortBuilder =
                SortBuilders.fieldSort(fieldName).setNestedSort(nestedSort);

        switch (order) {
            case ASC:
                sortBuilder.order(SortOrder.ASC);
                break;
            case DESC:
            default:
                sortBuilder.order(SortOrder.DESC);
        }
        return sortBuilder;
    }

    public static SortBuilder<FieldSortBuilder> buildSort(SortField sortField,
            Map<String, QueryBuilder> filterQueries) {

        String path;
        String fieldName;
        DataSortOrder order;

        path = StringUtils.isBlank(sortField.getPath())
                ? ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS
                : sortField.getPath();
        fieldName = path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS)
                ? sortField.getName()
                : path + '.' + sortField.getName();
        order = sortField.getOrder() != null ? sortField.getOrder()
                : DataSortOrder.DESC;
        SortBuilder<FieldSortBuilder> sortBuilder;

        if (path.equals(ESConstants.DUMMY_PATH_FOR_OUTERMOST_FIELDS)) {
            switch (order) {
                case ASC:
                    sortBuilder = SortBuilders.fieldSort(sortField.getName())
                            .order(SortOrder.ASC);
                    break;
                case DESC:
                default:
                    sortBuilder = SortBuilders.fieldSort(sortField.getName())
                            .order(SortOrder.DESC);
                    break;
            }
        } else {
            sortBuilder = buildNestedSort(fieldName, path, order, filterQueries);

        }
        return sortBuilder;
    }
}
