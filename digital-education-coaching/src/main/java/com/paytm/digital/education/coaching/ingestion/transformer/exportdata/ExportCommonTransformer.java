package com.paytm.digital.education.coaching.ingestion.transformer.exportdata;

import com.paytm.digital.education.enums.CourseLevel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

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

    static String convertCourseLevels(final List<CourseLevel> courseLevels) {
        if (CollectionUtils.isEmpty(courseLevels)) {
            return EMPTY_STRING;
        }

        final List<String> courseLevelNameList = courseLevels.stream()
                .map(CourseLevel::getDisplayName)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(courseLevelNameList)) {
            return EMPTY_STRING;
        }
        return StringUtils.join(courseLevelNameList, ",");
    }
}
