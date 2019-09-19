package com.paytm.digital.education.coaching.ingestion.transformer.exportdata;

import com.paytm.digital.education.enums.CourseType;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;

public class ExportCommonTransformer {

    private static final String YES = "Yes";
    private static final String NO  = "No";

    static String convertBooleanToString(final Boolean input) {
        if (null == input) {
            return NO;
        }
        return input ? YES : NO;
    }

    static String convertCourseTypes(final List<CourseType> courseTypeList) {
        if (CollectionUtils.isEmpty(courseTypeList)) {
            return EMPTY_STRING;
        }
        final StringBuilder courseTypeStringBuilder = new StringBuilder();
        courseTypeStringBuilder.append(courseTypeList.get(0).getText());

        for (int i = 1; i < courseTypeList.size(); i++) {
            courseTypeStringBuilder.append(",");
            courseTypeStringBuilder.append(courseTypeList.get(i));
        }
        return courseTypeStringBuilder.toString();
    }
}
