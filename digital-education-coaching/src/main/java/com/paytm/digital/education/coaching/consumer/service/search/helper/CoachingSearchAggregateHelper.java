package com.paytm.digital.education.coaching.consumer.service.search.helper;

import com.paytm.digital.education.elasticsearch.enums.AggregationType;
import com.paytm.digital.education.elasticsearch.enums.BucketAggregationSortParms;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.BucketSort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_CENTER_CITY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_CENTER_STATE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_DURATION;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_EXAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_INSTITUTE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_LEVEL;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_STREAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_EXAM_STREAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_INSTITUTE_COURSE_TYPES;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_INSTITUTE_EXAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_INSTITUTE_STREAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE_TYPE;
import static com.paytm.digital.education.constant.ExploreConstants.LINGUISTIC_MEDIUM;
import static com.paytm.digital.education.constant.ExploreConstants.SEARCH_EXAM_LEVEL;
import static com.paytm.digital.education.elasticsearch.enums.AggregationType.TERMS;
import static com.paytm.digital.education.elasticsearch.enums.AggregationType.TOP_HITS;

@Service
public class CoachingSearchAggregateHelper {

    public AggregateField[] getExamAggregateData() {
        List<String> examKeys =
                Arrays.asList(LINGUISTIC_MEDIUM, SEARCH_EXAM_LEVEL, COACHING_EXAM_STREAMS);
        List<AggregationType> examAggregateType =
                Arrays.asList(TERMS, TERMS, TERMS);
        BucketSort countDescSort = BucketSort.builder().key(BucketAggregationSortParms.COUNT).order(
                DataSortOrder.DESC).build();

        List<BucketSort> examSortOrder =
                Arrays.asList(countDescSort, countDescSort, countDescSort);
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

    public AggregateField[] getCoachingCourseAggregateData() {
        List<String> coachingCourseKeys =
                Arrays.asList(COACHING_COURSE_STREAMS, COACHING_COURSE_EXAMS,
                        COURSE_TYPE, COACHING_COURSE_INSTITUTE, COACHING_COURSE_LEVEL,
                        COACHING_COURSE_DURATION);
        List<AggregationType> coachingCourseAggregationType =
                Arrays.asList(TERMS, TERMS, TERMS, TERMS, TERMS, TERMS);
        BucketSort countDescSort = BucketSort.builder().key(BucketAggregationSortParms.COUNT).order(
                DataSortOrder.DESC).build();

        List<BucketSort> coachingCourseSortOrder =
                Arrays.asList(countDescSort, countDescSort, countDescSort, countDescSort,
                        countDescSort, countDescSort);
        AggregateField[] coachingCourseAggregateData =
                new AggregateField[coachingCourseKeys.size()];

        for (int i = 0; i < coachingCourseKeys.size(); i++) {
            AggregateField aggregateField = new AggregateField();
            aggregateField.setName(coachingCourseKeys.get(i));
            aggregateField.setType(coachingCourseAggregationType.get(i));
            aggregateField.setBucketsOrder(coachingCourseSortOrder.get(i));
            coachingCourseAggregateData[i] = aggregateField;
        }
        return coachingCourseAggregateData;
    }

    public AggregateField[] getCoachingInstituteAggregateData() {
        List<String> coachingInstituteKeys =
                Arrays.asList(COACHING_INSTITUTE_STREAMS, COACHING_INSTITUTE_EXAMS,
                        COACHING_INSTITUTE_COURSE_TYPES);
        List<AggregationType> coachingInstituteAggregateType =
                Arrays.asList(TERMS, TERMS, TERMS);
        BucketSort countDescSort = BucketSort.builder().key(BucketAggregationSortParms.COUNT).order(
                DataSortOrder.DESC).build();

        List<BucketSort> coachingInstituteSortOrder =
                Arrays.asList(countDescSort, countDescSort, countDescSort);
        AggregateField[] coachingInstituteAggregateData =
                new AggregateField[coachingInstituteKeys.size()];

        for (int i = 0; i < coachingInstituteKeys.size(); i++) {
            AggregateField aggregateField = new AggregateField();
            aggregateField.setName(coachingInstituteKeys.get(i));
            aggregateField.setType(coachingInstituteAggregateType.get(i));
            aggregateField.setBucketsOrder(coachingInstituteSortOrder.get(i));
            coachingInstituteAggregateData[i] = aggregateField;
        }
        return coachingInstituteAggregateData;
    }

    public AggregateField[] getCoachingCenterAggregateData() {
        List<String> coachingCenterKeys =
                Arrays.asList(COACHING_CENTER_CITY, COACHING_CENTER_STATE);
        List<AggregationType> coachingCenterType =
                Arrays.asList(TERMS, TERMS);
        BucketSort countDescSort = BucketSort.builder().key(BucketAggregationSortParms.COUNT).order(
                DataSortOrder.DESC).build();

        List<BucketSort> coachingCenterSortOrder =
                Arrays.asList(countDescSort, countDescSort);
        AggregateField[] coachingCenterAggregateData =
                new AggregateField[coachingCenterKeys.size()];

        for (int i = 0; i < coachingCenterKeys.size(); i++) {
            AggregateField aggregateField = new AggregateField();
            aggregateField.setName(coachingCenterKeys.get(i));
            aggregateField.setType(coachingCenterType.get(i));
            aggregateField.setBucketsOrder(coachingCenterSortOrder.get(i));
            coachingCenterAggregateData[i] = aggregateField;
        }
        return coachingCenterAggregateData;
    }

    public AggregateField[] getTopHitsAggregateData(String fieldName) {
        AggregateField[] aggregateFields = new AggregateField[1];
        aggregateFields[0] = new AggregateField();
        aggregateFields[0].setName(fieldName);
        aggregateFields[0].setType(TOP_HITS);
        return aggregateFields;
    }

}
