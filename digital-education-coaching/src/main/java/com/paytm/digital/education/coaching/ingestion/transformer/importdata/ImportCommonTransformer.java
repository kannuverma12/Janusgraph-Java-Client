package com.paytm.digital.education.coaching.ingestion.transformer.importdata;

import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ImportCommonTransformer {

    private static final Logger log = LoggerFactory.getLogger(ImportCommonTransformer.class);

    private static final String YES             = "Yes";
    private static final String DELIMITER_COMMA = ",";

    static boolean convertStringToBoolean(final String input) {
        if (StringUtils.isEmpty(input)) {
            return false;
        }
        return YES.equalsIgnoreCase(input.trim());
    }

    static List<CourseType> convertCourseTypes(final String courseType) {
        if (StringUtils.isEmpty(courseType)) {
            return new ArrayList<>();
        }
        final String[] courseTypes = courseType.split(DELIMITER_COMMA);
        final List<CourseType> courseTypeEnumList = new ArrayList<>();

        for (final String ct : courseTypes) {
            final CourseType convertedCourseType = CourseType.fromString(ct.trim());
            if (null != convertedCourseType) {
                courseTypeEnumList.add(convertedCourseType);
            }
        }
        return courseTypeEnumList;
    }

    public static List<CourseLevel> convertCourseLevels(final String courseLevel) {
        if (StringUtils.isEmpty(courseLevel)) {
            return new ArrayList<>();
        }
        final String[] courseLevels = courseLevel.split(DELIMITER_COMMA);
        final List<CourseLevel> courseLevelEnumList = new ArrayList<>();

        for (final String ct : courseLevels) {
            final CourseLevel convertedCourseLevel = CourseLevel.fromString(ct);
            if (null != convertedCourseLevel) {
                courseLevelEnumList.add(convertedCourseLevel);
            }
        }
        return courseLevelEnumList;
    }

    static List<Long> convertStringToListOfLong(final String input) {
        if (StringUtils.isEmpty(input)) {
            return new ArrayList<>();
        }

        final String[] commaSeparatedValues = input.split(DELIMITER_COMMA);
        final List<Long> list = new ArrayList<>();

        for (final String value : commaSeparatedValues) {
            list.add(Long.valueOf(value.trim()));
        }

        return list;
    }

    static List<String> convertStringToListOfString(final String input) {
        if (StringUtils.isEmpty(input)) {
            return new ArrayList<>();
        }

        final String[] commaSeparatedValues = input.split(DELIMITER_COMMA);
        final List<String> list = new ArrayList<>();

        for (final String value : commaSeparatedValues) {
            list.add(value.trim());
        }

        return list;
    }
}
