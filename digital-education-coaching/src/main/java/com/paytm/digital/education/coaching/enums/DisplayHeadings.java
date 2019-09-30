package com.paytm.digital.education.coaching.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DisplayHeadings {

    TOP_RANKERS("Top Rankers"),
    IMPORTANT_DATES("Important Dates"),
    COURSE_FEATURES_AVAILABLE("Features Available"),
    COURSE_DETAILS("Course Details"),
    DOWNLOAD_SYLLABUS_AND_BROCHURE("Download Syllabus & Brochure"),
    COURSE_FEE("Course Fee"),
    COURSE_TYPE("Course Type"),
    LANGUAGE("Language"),
    PROVIDES_CERTIFICATE("Provides Certification"),
    DOUBT_SOLVING_SESSIONS("Doubt Solving Sessions"),
    PROGRESS_ANALYSIS("Progress Analysis"),
    RANK_ANALYSIS("Rank Analysis"),
    LECTURE_COUNT("Lecture Count"),
    LECTURE_DURATION("Lecture Duration"),
    TEST_COUNT("Test Count"),
    TEACHER_STUDENT_RATIO("Teacher Student Ratio"),
    ONLINE_TEST_COUNT("Online Test Count"),
    PRACTICE_PAPER_COUNT("Practice Paper Count"),
    TEST_DURATION("Test Duration"),
    TEST_PRACTICE_PAPER_COUNT("Test Practice Paper Count"),
    ASSIGNMENTS_COUNT("Assignments Count"),
    BOOKS_COUNT("Books Count"),
    SOLVED_PAPER_COUNT("Solved Paper Count"),

    FIND_CENTERS("Find Centers"),
    FIND_CENTERS_DESCRIPTION("Locate %s Centers near you"),
    DOWNLOAD_BROCHURE("Download Brochure"),
    BROWSE_BY_COURSE_TYPE("Browse by Course Type"),
    TOP_COACHING_COURSES_BY("Top Coaching Courses by %s"),
    TOP_EXAMS_PREPARED_FOR_BY("Top Exams prepared for by %s"),
    STREAMS_PREPARED_FOR_BY("Streams prepared for by %s"),

    TOP_COACHING_COURSES_FOR("Top Coaching Courses for %s"),
    TOP_COACHING_INSTITUTES_FOR("Top Coaching Institutes for %s"),
    COACHING_FOR("Coaching for %s Exams"),

    SIMILAR_COACHING_COURSES("Similar Coaching Courses"),
    SIMILAR_COACHING_INSTITUTES("Similar Coaching Institutes"),
    ALL_YOU_NEED_TO_KNOW_ABOUT("All you need to know about %s");

    private String value;
}
