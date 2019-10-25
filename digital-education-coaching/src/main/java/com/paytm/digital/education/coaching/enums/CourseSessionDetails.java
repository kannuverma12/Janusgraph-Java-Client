package com.paytm.digital.education.coaching.enums;

import com.paytm.digital.education.enums.CourseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.paytm.digital.education.coaching.enums.DisplayHeadings.ASSIGNMENTS_COUNT;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.BOOKS_COUNT;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.LECTURE_COUNT;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.LECTURE_DURATION;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.ONLINE_TEST_COUNT;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.PRACTICE_PAPER_COUNT;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.SOLVED_PAPER_COUNT;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.TEACHER_STUDENT_RATIO;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.TEST_COUNT;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.TEST_DURATION;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.TEST_PRACTICE_PAPER_COUNT;

@Getter
@AllArgsConstructor
@Component
public class CourseSessionDetails {

    private static final Map<CourseType, List<Session>> COURSE_TYPE_AND_SESSIONS_MAP =
            new HashMap<>();

    private static List<Session> SESSIONS_LIST;

    static {
        COURSE_TYPE_AND_SESSIONS_MAP.put(CourseType.CLASSROOM_COURSE, new ArrayList<Session>() {
            {
                this.add(new Session(LECTURE_COUNT.getValue(),
                        "classroomLectureCount"));
                this.add(new Session(LECTURE_DURATION.getValue(),
                        "classroomLectureDuration"));
                this.add(new Session(TEST_COUNT.getValue(),
                        "classroomTestCount"));
                this.add(new Session(TEACHER_STUDENT_RATIO.getValue(),
                        "classroomTeacherStudentRatio"));
            }
        });

        COURSE_TYPE_AND_SESSIONS_MAP.put(CourseType.TEST_SERIES_ONLINE, new ArrayList<Session>() {
            {
                this.add(new Session(LECTURE_COUNT.getValue(),
                        "elearningLectureCount"));
                this.add(new Session(LECTURE_DURATION.getValue(),
                        "elearningLectureDuration"));
                this.add(new Session(ONLINE_TEST_COUNT.getValue(),
                        "elearningOnlineTestCount"));
                this.add(new Session(PRACTICE_PAPER_COUNT.getValue(),
                        "elearningPracticePaperCount"));
            }
        });

        COURSE_TYPE_AND_SESSIONS_MAP.put(CourseType.TEST_SERIES_OFFLINE, new ArrayList<Session>() {
            {
                this.add(new Session(TEST_COUNT.getValue(), "testCount"));
                this.add(new Session(TEST_DURATION.getValue(), "testDuration"));
                this.add(new Session(TEST_PRACTICE_PAPER_COUNT.getValue(),
                        "testPracticePaperCount"));
            }
        });

        COURSE_TYPE_AND_SESSIONS_MAP.put(CourseType.DISTANCE_LEARNING, new ArrayList<Session>() {
            {
                this.add(new Session(ASSIGNMENTS_COUNT.getValue(),
                        "distanceLearningAssignmentsCount"));
                this.add(new Session(BOOKS_COUNT.getValue(),
                        "distanceLearningBooksCount"));
                this.add(new Session(SOLVED_PAPER_COUNT.getValue(),
                        "distanceLearningSolvedPaperCount"));
            }
        });

        SESSIONS_LIST = COURSE_TYPE_AND_SESSIONS_MAP.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public static List<Session> getSessionsList() {
        return SESSIONS_LIST;
    }

    public static Map<CourseType, List<Session>> getCourseTypeAndSessionsMap() {
        return COURSE_TYPE_AND_SESSIONS_MAP;
    }


    @Getter
    @AllArgsConstructor
    public static class Session {
        private String displayName;
        private String dbFieldName;
    }
}
