package com.paytm.digital.education.coaching.ingestion.transformer.importdata;

import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.CourseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.DATE_TIME_FORMATTER;

@Slf4j
public class ImportCommonTransformer {

    private static final String YES             = "Yes";
    private static final String DELIMITER_COMMA = ",";

    static boolean convertStringToBoolean(final String input) {
        if (StringUtils.isEmpty(input)) {
            return false;
        }
        return YES.equalsIgnoreCase(input);
    }

    static List<CourseType> convertCourseTypes(final String courseType) {
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
}
