package com.paytm.education.elasticsearch;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.junit.Test;
import com.paytm.digital.education.elasticsearch.enums.AggregationType;
import com.paytm.digital.education.elasticsearch.enums.BucketAggregationSortParms;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.BucketSort;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.SearchField;
import com.paytm.digital.education.elasticsearch.models.SortField;
import com.paytm.digital.education.elasticsearch.service.ElasticSearchService;
import com.paytm.digital.education.elasticsearch.service.impl.ElasticSearchServiceImpl;

public class Tests {

    @Test
    public void buildAutoSuggestRequest() {
        ElasticRequest elasticRequest = new ElasticRequest();
        elasticRequest.setIndex("college_2019_02_14");
        elasticRequest.setAnalyzer("word_delimiter_analyzer");
        elasticRequest.setQueryTerm("institute");
        elasticRequest.setOffSet(0);
        elasticRequest.setLimit(10);
        elasticRequest.setSearchRequest(true);
        elasticRequest.setIsFiltersRequest(true);


        // List<String> ent= new ArrayList<> ();
        // ent.add("college");
        // FilterField[] filterFields = new FilterField[1];
        // filterFields[0] = new FilterField();
        // filterFields[0].setName("entity");
        // filterFields[0].setType(FilterQueryType.TERMS);
        // filterFields[0].setValues(ent);
        //
        // elasticRequest.setFilterFields(filterFields);

        SearchField[] searchFields = new SearchField[1];
        searchFields[0] = new SearchField();
        searchFields[0].setName("names");

        SortField[] sortFields = new SortField[1];
        sortFields[0] = new SortField();
        sortFields[0].setName("_score");
        sortFields[0].setOrder(DataSortOrder.DESC);

        AggregateField[] aggFields = new AggregateField[2];
        aggFields[0] = new AggregateField();
        aggFields[0].setName("official_name");
        aggFields[0].setPath("");
        aggFields[0].setType(AggregationType.TERMS);
        BucketSort sort = new BucketSort();
        sort.setKey(BucketAggregationSortParms.COUNT);
        sort.setOrder(DataSortOrder.ASC);
        aggFields[0].setBucketsOrder(sort);

        aggFields[0] = new AggregateField();
        aggFields[0].setName("level");
        aggFields[0].setPath("courses");
        aggFields[0].setType(AggregationType.TERMS);
        BucketSort sort2 = new BucketSort();
        sort2.setKey(BucketAggregationSortParms.KEY);
        sort2.setOrder(DataSortOrder.ASC);
        aggFields[0].setBucketsOrder(sort2);

        elasticRequest.setAggregateFields(aggFields);

        elasticRequest.setSortFields(sortFields);

        elasticRequest.setSearchFields(searchFields);

        ElasticSearchService<Tests> service = new ElasticSearchServiceImpl<>();
        try {
            service.executeSearch(elasticRequest, Tests.class);
        } catch (IOException | TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}
