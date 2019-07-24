package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.elasticsearch.enums.AggregationType.MINMAX;
import static com.paytm.digital.education.elasticsearch.enums.AggregationType.TERMS;
import static com.paytm.digital.education.elasticsearch.enums.AggregationType.TOP_HITS;
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
import static com.paytm.digital.education.explore.constants.ExploreConstants.BRANCH_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DEGREE_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_NAME_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STREAM_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.LEVEL_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_HISTORY_USERID;

import com.paytm.digital.education.elasticsearch.enums.AggregationType;
import com.paytm.digital.education.elasticsearch.enums.BucketAggregationSortParms;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.BucketSort;

import java.util.Arrays;
import java.util.List;

import com.paytm.digital.education.explore.enums.Client;
import org.springframework.stereotype.Service;

@Service
public class SearchAggregateHelper {

    public AggregateField[] getInstituteAggregateData() {
        List<String> instituteKeys =
                Arrays.asList(EXAMS_ACCEPTED_INSTITUTE, STREAM_INSTITUTE, COURSE_LEVEL_INSTITUTE,
                        STATE_INSTITUTE, CITY_INSTITUTE, FEES_INSTITUTE, OWNERSHIP, FACILITIES,
                        INSTITUTE_GENDER, ESTABLISHMENT_YEAR);
        List<AggregationType> instituteAggregateType =
                Arrays.asList(TERMS, TERMS, TERMS, TERMS, TERMS, MINMAX, TERMS, TERMS, TERMS,
                        MINMAX);
        BucketSort countDescSort = BucketSort.builder().key(BucketAggregationSortParms.COUNT).order(
                DataSortOrder.DESC).build();

        List<BucketSort> instituteSortOrder =
                Arrays.asList(countDescSort, countDescSort, countDescSort, countDescSort,
                        countDescSort, countDescSort, countDescSort, countDescSort, countDescSort,
                        null, null);

        AggregateField[] instituteAggregateData = new AggregateField[instituteKeys.size()];

        for (int i = 0; i < instituteKeys.size(); i++) {
            AggregateField aggregateField = new AggregateField();
            aggregateField.setName(instituteKeys.get(i));
            aggregateField.setType(instituteAggregateType.get(i));
            aggregateField.setBucketsOrder(instituteSortOrder.get(i));
            instituteAggregateData[i] = aggregateField;
        }
        return instituteAggregateData;
    }

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

    public AggregateField[] getCourseAggregateData(Client client) {

        if (Client.APP.equals(client)) {
            AggregateField[] courseAggregateData = new AggregateField[1];
            courseAggregateData[0] = new AggregateField();
            courseAggregateData[0].setName(LEVEL_COURSE);
            courseAggregateData[0].setType(TOP_HITS);
            return courseAggregateData;
        }
        List<String> courseKeys =
                Arrays.asList(DEGREE_COURSE, BRANCH_COURSE, INSTITUTE_NAME_COURSE, STREAM_COURSE,
                        LEVEL_COURSE);
        List<AggregationType> courseAggregateType =
                Arrays.asList(TERMS, TERMS, TERMS, TERMS, TERMS);
        BucketSort countDescSort = BucketSort.builder().key(BucketAggregationSortParms.COUNT).order(
                DataSortOrder.DESC).build();

        List<BucketSort> examSortOrder =
                Arrays.asList(countDescSort, countDescSort, countDescSort, countDescSort,
                        countDescSort);
        AggregateField[] courseAggregateData = new AggregateField[courseKeys.size()];

        for (int i = 0; i < courseKeys.size(); i++) {
            AggregateField aggregateField = new AggregateField();
            aggregateField.setName(courseKeys.get(i));
            aggregateField.setType(courseAggregateType.get(i));
            aggregateField.setBucketsOrder(examSortOrder.get(i));
            courseAggregateData[i] = aggregateField;
        }
        return courseAggregateData;
    }

    public AggregateField[] gerRecentSearchesAggregateData() {
        AggregateField[] aggregateFields = new AggregateField[1];
        aggregateFields[0] = new AggregateField();
        aggregateFields[0].setName(SEARCH_HISTORY_USERID);
        aggregateFields[0].setType(TERMS);
        aggregateFields[0].setBucketsOrder(
                BucketSort.builder().key(BucketAggregationSortParms.COUNT).order(DataSortOrder.DESC)
                        .build());
        return aggregateFields;
    }
}
