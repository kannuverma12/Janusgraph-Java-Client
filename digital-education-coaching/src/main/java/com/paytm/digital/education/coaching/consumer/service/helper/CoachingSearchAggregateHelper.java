package com.paytm.digital.education.coaching.consumer.service.helper;

import com.paytm.digital.education.elasticsearch.enums.AggregationType;
import com.paytm.digital.education.elasticsearch.enums.BucketAggregationSortParms;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.BucketSort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.paytm.digital.education.constant.ExploreConstants.LINGUISTIC_MEDIUM;
import static com.paytm.digital.education.constant.ExploreConstants.SEARCH_EXAM_LEVEL;
import static com.paytm.digital.education.elasticsearch.enums.AggregationType.TERMS;
import static com.paytm.digital.education.elasticsearch.enums.AggregationType.TOP_HITS;

@Service
public class CoachingSearchAggregateHelper {

    public AggregateField[] getExamAggregateData() {
        List<String> examKeys =
                Arrays.asList(LINGUISTIC_MEDIUM, SEARCH_EXAM_LEVEL);
        List<AggregationType> examAggregateType =
                Arrays.asList(TERMS, TERMS);
        BucketSort countDescSort = BucketSort.builder().key(BucketAggregationSortParms.COUNT).order(
                DataSortOrder.DESC).build();

        List<BucketSort> examSortOrder =
                Arrays.asList(countDescSort, countDescSort);
        AggregateField[] examAggregateData = new AggregateField[examKeys.size()];

        for (int i = 0; i < examKeys.size(); i++) {
            AggregateField aggregateField = new AggregateField();
            aggregateField.setName(examKeys.get(i));
            aggregateField.setType(examAggregateType.get(i));
            aggregateField.setBucketsOrder(examSortOrder.get(i));
            examAggregateData[i] = aggregateField;
        }
        return examAggregateData;
    }

    public AggregateField[] getTopHitsAggregateData(String fieldName) {
        AggregateField[] aggregateFields = new AggregateField[1];
        aggregateFields[0] = new AggregateField();
        aggregateFields[0].setName(fieldName);
        aggregateFields[0].setType(TOP_HITS);
        return aggregateFields;
    }

}
