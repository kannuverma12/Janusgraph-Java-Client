package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.elasticsearch.enums.AggregationType;
import com.paytm.digital.education.elasticsearch.enums.BucketAggregationSortParms;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.BucketSort;
import com.paytm.digital.education.explore.enums.Client;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.paytm.digital.education.elasticsearch.enums.AggregationType.GEO_DISTANCE;
import static com.paytm.digital.education.elasticsearch.enums.AggregationType.MINMAX;
import static com.paytm.digital.education.elasticsearch.enums.AggregationType.TERMS;
import static com.paytm.digital.education.elasticsearch.enums.AggregationType.TOP_HITS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.BRANCH_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CITY_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_LEVEL_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DEGREE_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ESTABLISHMENT_YEAR;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAMS_ACCEPTED_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.FACILITIES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.FEES_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_GENDER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_NAME_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.LEVEL_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.LINGUISTIC_MEDIUM;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OWNERSHIP;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_EXAM_DOMAIN;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_EXAM_LEVEL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_HISTORY_USERID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STATE_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STREAM_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STREAM_INSTITUTE;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_BOARDS_ACCEPTED;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_BOARDS_EDUCATION_LEVEL;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_BOARDS_ESTABLISHMENT_YEAR;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_BOARDS_FEE;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_BOARDS_GENDER_ACCEPTED;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_BOARDS_OWNERSHIP;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_CITY;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_FACILITIES;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_LOCATION;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_MEDIUM;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_STATE;

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
                Arrays.asList(LINGUISTIC_MEDIUM, SEARCH_EXAM_LEVEL, SEARCH_EXAM_DOMAIN);
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

    public AggregateField[] getSchoolAggregateData(
            SearchRequest schoolSearchRequest) {
        List<String> schoolKeys =
                Arrays.asList(SCHOOL_CITY, SCHOOL_STATE, SCHOOL_BOARDS_EDUCATION_LEVEL,
                        SCHOOL_BOARDS_OWNERSHIP, SCHOOL_BOARDS_GENDER_ACCEPTED,
                        SCHOOL_FACILITIES, SCHOOL_BOARDS_ACCEPTED,
                        SCHOOL_MEDIUM, SCHOOL_BOARDS_FEE, SCHOOL_BOARDS_ESTABLISHMENT_YEAR,SCHOOL_LOCATION);
        List<AggregationType> schoolAggregateType =
                Arrays.asList(TERMS, TERMS, TERMS, TERMS, TERMS, TERMS, TERMS,
                        TERMS, MINMAX, MINMAX, GEO_DISTANCE);
        BucketSort countDescSort = BucketSort.builder().key(BucketAggregationSortParms.COUNT).order(
                DataSortOrder.DESC).build();

        List<BucketSort> schoolSortOrder =
                Arrays.asList(countDescSort, countDescSort, countDescSort, countDescSort, countDescSort,
                        countDescSort, countDescSort, countDescSort, null, null, null);

        AggregateField[] schoolAggregateData = new AggregateField[schoolKeys.size()];

        for (int i = 0; i < schoolKeys.size(); i++) {
            AggregateField aggregateField = new AggregateField();
            aggregateField.setName(schoolKeys.get(i));
            aggregateField.setType(schoolAggregateType.get(i));
            aggregateField.setBucketsOrder(schoolSortOrder.get(i));

            if (schoolAggregateType.get(i).equals(GEO_DISTANCE) && Objects
                    .nonNull(schoolSearchRequest.getGeoLocation())
                    && Objects.nonNull(schoolSearchRequest.getGeoLocation().getLat()) && Objects
                    .nonNull(schoolSearchRequest.getGeoLocation().getLon())) {
                String[] values = {String.valueOf(schoolSearchRequest.getGeoLocation().getLat()),
                        String.valueOf(schoolSearchRequest.getGeoLocation().getLon())};
                aggregateField.setValues(values);
            }
            schoolAggregateData[i] = aggregateField;
        }
        return schoolAggregateData;
    }

    public AggregateField[] getTopHitsAggregateData(List<String> dataPerFilter) {
        AggregateField[] aggregateFields = new AggregateField[1];
        aggregateFields[0] = new AggregateField();
        if (!CollectionUtils.isEmpty(dataPerFilter)) {
            aggregateFields[0].setName(dataPerFilter.get(0));
            if (dataPerFilter.size() == 2 && StringUtils.isNotBlank(dataPerFilter.get(1))) {
                aggregateFields[0].setChildTermsFieldName(dataPerFilter.get(1));
            }
        }
        aggregateFields[0].setType(TOP_HITS);
        return aggregateFields;
    }
}
