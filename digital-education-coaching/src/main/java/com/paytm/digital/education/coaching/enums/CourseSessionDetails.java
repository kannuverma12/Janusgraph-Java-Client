package com.paytm.digital.education.coaching.enums;

import com.paytm.digital.education.enums.CourseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@Component
public class CourseSessionDetails {

    public static Map<CourseType, List<Session>> getCourseTypeAndSessionsMap() {
        return COURSE_TYPE_AND_SESSIONS_MAP;
    }

    private static final Map<CourseType, List<Session>> COURSE_TYPE_AND_SESSIONS_MAP =
            new HashMap<>();

    static {
        COURSE_TYPE_AND_SESSIONS_MAP.put(CourseType.CLASSROOM_COURSE, new ArrayList<Session>() {
            {
                this.add(new Session("Lecture Count",
                        "classroomLectureCount"));
                this.add(new Session("Lecture Duration",
                        "classroomLectureDuration"));
                this.add(new Session("Test Count",
                        "classroomTestCount"));
                this.add(new Session("Teacher Student Ratio",
                        "classroomTeacherStudentRatio"));
            }
        });

        COURSE_TYPE_AND_SESSIONS_MAP.put(CourseType.TEST_SERIES_ONLINE, new ArrayList<Session>() {
            {
                this.add(new Session("Lecture Count",
                        "elearningLectureCount"));
                this.add(new Session("Lecture Duration",
                        "elearningLectureDuration"));
                this.add(new Session("Online Test Count",
                        "elearningOnlineTestCount"));
                this.add(new Session("Practice Paper Count",
                        "elearningPracticePaperCount"));

            }
        });

        COURSE_TYPE_AND_SESSIONS_MAP.put(CourseType.TEST_SERIES_OFFLINE, new ArrayList<Session>() {
            {
                this.add(new Session("Test Count",
                        "testCount"));
                this.add(new Session("Test Duration",
                        "testDuration"));
                this.add(new Session("Test Practice Paper Count",
                        "testPracticePaperCount"));
            }
        });

        COURSE_TYPE_AND_SESSIONS_MAP.put(CourseType.DISTANCE_LEARNING, new ArrayList<Session>() {
            {
                this.add(new Session("Assignments Count",
                        "distanceLearningAssignmentsCount"));
                this.add(new Session("Books Count",
                        "distanceLearningBooksCount"));
                this.add(new Session("Solved Paper Count",
                        "distanceLearningSolvedPaperCount"));
            }
        });
    }


    @Getter
    @AllArgsConstructor
    public static class Session {
        private String displayName;
        private String dbFieldName;
    }
}
