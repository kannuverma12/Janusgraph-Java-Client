package com.paytm.digital.education.coaching.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum CoachingCourseFeatureName {

    TEACHING_METHODOLOGY("Teaching methodology"),
    ONLINE_TEST_SERIES("Online test series"),
    ONE_ON_ONE_COUNSELLING("One on one counselling"),
    COURSE_MATERIAL("Course material"),
    PERFORMANCE_TRACKER("Performance tracker"),
    BOARD_EXAM_PREPARATION("Board Exam Preparation"),
    DOUBT_SOLVING_SESSIONS("Doubt Solving Sessions"),
    PARENT_TEACHER_MEETING("Parent Teacher Meeting"),
    NATIONAL_LEVEL_TEST_SERIES("National level test series"),
    VIDEO_COURSES("Video Courses"),
    MENTORSHIP("Mentorship"),
    LEARNING_ENVIRONMENT("Learning Environment"),
    NATIONAL_PRESENCE_NETWORK("National Presence & Network"),
    JOB_OPPORTUNITIES_VACANCY("Job opportunities / Vacancy"),
    FACULTY("Faculty"),
    INTEGRATED_SCHOOL_PROGRAMS("Integrated School Programs"),
    HOSTEL_FACILITY("Hostel Facility"),
    DISCUSSION_FORUM_("Discussion Forum "),
    SCHOLARSHIPS("Scholarships"),
    REWARDS_FOR_TOP_RANKERS("Rewards for top rankers"),
    CONVEYANCE("Conveyance"),
    LIBRARY_FACILITY("Library facility"),
    ALUMNI_SUPPORT("Alumni Support");

    private String text;

    CoachingCourseFeatureName(String text) {
        this.text = text;
    }

    public static CoachingCourseFeatureName fromString(String text) {
        for (CoachingCourseFeatureName coachingCourseFeatureName :
                CoachingCourseFeatureName.values()) {
            if (coachingCourseFeatureName.getText().equalsIgnoreCase(text)) {
                return coachingCourseFeatureName;
            }
        }
        log.error("Could not convert text to enum val, text: {}", text);
        return null;
    }

    @JsonValue
    public String getText() {
        return this.text;
    }
}
