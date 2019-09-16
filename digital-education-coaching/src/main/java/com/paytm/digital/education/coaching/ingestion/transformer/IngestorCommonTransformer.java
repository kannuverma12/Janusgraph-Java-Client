package com.paytm.digital.education.coaching.ingestion.transformer;

import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.CourseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class IngestorCommonTransformer {

    private static final String YES             = "yes";
    private static final String DELIMITER_COMMA = ",";

    public static boolean convertStringToBoolean(final String input) {
        if (StringUtils.isEmpty(input)) {
            return false;
        }
        return YES.equalsIgnoreCase(input);
    }

    public static List<CourseType> convertCourseTypes(final String courseType) {
        if (StringUtils.isEmpty(courseType)) {
            return new ArrayList<>();
        }
        final String[] courseTypes = courseType.split(DELIMITER_COMMA);
        final List<CourseType> courseTypeEnumList = new ArrayList<>();

        for (final String ct : courseTypes) {
            final CourseType convertedCourseType = CourseType.fromString(ct);
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

    public static List<Long> convertStringToListOfLong(final String input) {
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
}
