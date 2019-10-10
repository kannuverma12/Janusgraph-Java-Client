package com.paytm.digital.education.coaching.enums;

import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CoachingCourseTypeResponse;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.utility.CommonUtil;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_TYPE_PLACEHOLDER;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_COURSE_TYPES;

@Getter
public enum CoachingCourseType {

    CLASSROOM_COURSE(CourseType.CLASSROOM_COURSE,
            CoachingCourseTypeResponse.builder()
                    .description("")
                    .imageUrl(CommonUtil.getAbsoluteUrl(COACHING_COURSE_TYPE_PLACEHOLDER,
                            COACHING_COURSE_TYPES))
                    .name(CourseType.CLASSROOM_COURSE.getText())
                    .enumName(CourseType.CLASSROOM_COURSE)
                    .build()),
    DISTANCE_LEARNING(CourseType.DISTANCE_LEARNING,
            CoachingCourseTypeResponse.builder()
                    .description("")
                    .imageUrl(CommonUtil.getAbsoluteUrl(COACHING_COURSE_TYPE_PLACEHOLDER,
                            COACHING_COURSE_TYPES))
                    .name(CourseType.DISTANCE_LEARNING.getText())
                    .enumName(CourseType.DISTANCE_LEARNING)
                    .build()),
    TEST_SERIES_ONLINE(CourseType.TEST_SERIES_ONLINE,
            CoachingCourseTypeResponse.builder()
                    .description("")
                    .imageUrl(CommonUtil.getAbsoluteUrl(COACHING_COURSE_TYPE_PLACEHOLDER,
                            COACHING_COURSE_TYPES))
                    .name(CourseType.TEST_SERIES_ONLINE.getText())
                    .enumName(CourseType.TEST_SERIES_ONLINE)
                    .build()),
    TEST_SERIES_OFFLINE(CourseType.TEST_SERIES_OFFLINE,
            CoachingCourseTypeResponse.builder()
                    .description("")
                    .imageUrl(CommonUtil.getAbsoluteUrl(COACHING_COURSE_TYPE_PLACEHOLDER,
                            COACHING_COURSE_TYPES))
                    .name(CourseType.TEST_SERIES_OFFLINE.getText())
                    .enumName(CourseType.TEST_SERIES_OFFLINE)
                    .build()),
    E_LEARNING(CourseType.E_LEARNING,
            CoachingCourseTypeResponse.builder()
                    .description("")
                    .imageUrl(CommonUtil.getAbsoluteUrl(COACHING_COURSE_TYPE_PLACEHOLDER,
                            COACHING_COURSE_TYPES))
                    .name(CourseType.E_LEARNING.getText())
                    .enumName(CourseType.E_LEARNING)
                    .build());

    private static Map<CourseType, CoachingCourseTypeResponse> COURSE_TYPE_STATIC_DATA_MAP;
    private final  CourseType                                  courseType;
    private final  CoachingCourseTypeResponse                  coachingCourseTypeResponse;

    CoachingCourseType(CourseType courseType,
            CoachingCourseTypeResponse coachingCourseTypeResponse) {
        this.courseType = courseType;
        this.coachingCourseTypeResponse = coachingCourseTypeResponse;
    }

    public static CoachingCourseTypeResponse getStaticDataByCourseType(CourseType courseType) {
        if (CollectionUtils.isEmpty(COURSE_TYPE_STATIC_DATA_MAP)) {
            setCourseTypeStaticDataMap();
        }
        return COURSE_TYPE_STATIC_DATA_MAP.get(courseType);
    }

    public static void setCourseTypeStaticDataMap() {
        COURSE_TYPE_STATIC_DATA_MAP = new HashMap<>();
        for (CoachingCourseType coachingCourseType : CoachingCourseType.values()) {
            COURSE_TYPE_STATIC_DATA_MAP.put(coachingCourseType.courseType,
                    coachingCourseType.getCoachingCourseTypeResponse());
        }
    }

}
