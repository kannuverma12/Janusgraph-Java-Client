package com.paytm.digital.education.explore.utility;

import static com.paytm.digital.education.constant.ExploreConstants.COURSE_CLASS;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_CLASS;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_CLASS;
import static com.paytm.digital.education.enums.EducationEntity.EXAM;
import static com.paytm.digital.education.enums.EducationEntity.COURSE;
import static com.paytm.digital.education.enums.EducationEntity.INSTITUTE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.paytm.digital.education.constant.ExploreConstants.COURSE_PREFIX;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_PREFIX;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_PREFIX;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FieldsRetrievalUtil {

    public <T> Map<String, ArrayList<String>> getFormattedFields(List<String> queryFields,
            String className) {
        ArrayList<String> instituteQueryFields = new ArrayList<>();
        ArrayList<String> courseQueryFields = new ArrayList<>();
        ArrayList<String> examQueryFields = new ArrayList<>();
        for (String field : queryFields) {
            String[] fieldString = field.split("\\.");
            if (fieldString.length > 1) {
                switch (fieldString[0] + '.') {
                    case INSTITUTE_PREFIX:
                        instituteQueryFields.add(fieldString[1]);
                        break;
                    case EXAM_PREFIX:
                        examQueryFields.add(fieldString[1]);
                        break;
                    case COURSE_PREFIX:
                        courseQueryFields.add(fieldString[1]);
                        break;
                    default:
                        break;
                }
            } else {
                switch (className) {
                    case COURSE_CLASS:
                        courseQueryFields.add(field);
                        break;
                    case INSTITUTE_CLASS:
                        instituteQueryFields.add(field);
                        break;
                    case EXAM_CLASS:
                        examQueryFields.add(field);
                        break;
                    default:
                        break;

                }
            }
        }
        Map<String, ArrayList<String>> fields = new HashMap<>();
        fields.put(COURSE.name().toLowerCase(), courseQueryFields);
        fields.put(EXAM.name().toLowerCase(), examQueryFields);
        fields.put(INSTITUTE.name().toLowerCase(), instituteQueryFields);
        return fields;
    }
}

