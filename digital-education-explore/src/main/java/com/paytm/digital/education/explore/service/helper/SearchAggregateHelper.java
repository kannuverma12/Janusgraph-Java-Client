package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.elasticsearch.enums.AggregationType;
import com.paytm.digital.education.elasticsearch.enums.BucketAggregationSortParms;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.BucketSort;

import java.util.Arrays;
import java.util.List;

import static com.paytm.digital.education.elasticsearch.enums.AggregationType.MINMAX;
import static com.paytm.digital.education.elasticsearch.enums.AggregationType.TERMS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CITY_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_LEVEL_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ESTABLISHMENT_YEAR;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAMS_ACCEPTED_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.FACILITIES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.FEES_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_GENDER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.LINGUISTIC_MEDIUM;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OWNERSHIP;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_EXAM_LEVEL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STATE_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STREAM_INSTITUTE;

public class SearchAggregateHelper {

    private static AggregateField[] instituteAggregateData;
    private static AggregateField[] examAggregateData;

    static {

        final BucketSort bucketSort =
                BucketSort.builder().key(BucketAggregationSortParms.KEY).order(
                        DataSortOrder.ASC).build();

        List<String> instituteKeys =
                Arrays.asList(STATE_INSTITUTE, CITY_INSTITUTE, STREAM_INSTITUTE,
                        COURSE_LEVEL_INSTITUTE, EXAMS_ACCEPTED_INSTITUTE, FEES_INSTITUTE, OWNERSHIP,
                        FACILITIES, INSTITUTE_GENDER, ESTABLISHMENT_YEAR);
        List<AggregationType> instituteAggregateType =
                Arrays.asList(TERMS, TERMS, TERMS, TERMS, TERMS, MINMAX, TERMS, TERMS, TERMS,
                        MINMAX);
        List<BucketSort> instituteSortOrder =
                Arrays.asList(bucketSort, bucketSort, bucketSort, bucketSort, bucketSort, null,
                        bucketSort, bucketSort, bucketSort, null);

        instituteAggregateData = new AggregateField[instituteKeys.size()];

        for (int i = 0; i < instituteKeys.size(); i++) {
            AggregateField aggregateField = new AggregateField();
            aggregateField.setName(instituteKeys.get(i));
            aggregateField.setType(instituteAggregateType.get(i));
            aggregateField.setBucketsOrder(instituteSortOrder.get(i));
            instituteAggregateData[i] = aggregateField;
        }

        List<String> examKeys =
                Arrays.asList(LINGUISTIC_MEDIUM, SEARCH_EXAM_LEVEL);
        List<AggregationType> examAggregateType =
                Arrays.asList(TERMS, TERMS);
        List<BucketSort> examSortOrder =
                Arrays.asList(bucketSort, bucketSort);

        examAggregateData = new AggregateField[examKeys.size()];

        for (int i = 0; i < examKeys.size(); i++) {
            AggregateField aggregateField = new AggregateField();
            aggregateField.setName(examKeys.get(i));
            aggregateField.setType(examAggregateType.get(i));
            aggregateField.setBucketsOrder(examSortOrder.get(i));
            examAggregateData[i] = aggregateField;
        }
    }

    public static AggregateField[] getInstituteAggregateData() {
        return instituteAggregateData;
    }

    public static AggregateField[] getExamAggregateData() {
        return examAggregateData;
    }
}
