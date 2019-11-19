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
    COURSE_HOW_TO_GET_STARTED("How to get started with %s"),
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
    TOP_COACHING_COURSES_BY("Top Courses by %s"),
    TOP_EXAMS_PREPARED_FOR_BY("Top Exams prepared for by %s"),
    STREAMS_PREPARED_FOR_BY("Streams prepared for by %s"),
    MORE_FROM("More from %s"),
    FAQ("FAQ's"),
    OTHER_INFORMATION("Other Information"),

    CLASSROOM_COURSE_DESCRIPTION("Extensive classroom program with rigorous teaching sessions"),
    DISTANCE_LEARNING_DESCRIPTION(
            "Get the best course material delivered doorstep for best self study"),
    TEST_SERIES_ONLINE_DESCRIPTION(
            "Become 100% exam ready by practicing & improving results with test series"),
    TEST_SERIES_OFFLINE_DESCRIPTION(
            "Become 100% exam ready by practicing & improving results with test series"),
    E_LEARNING_DESCRIPTION(
            "Become 100% exam ready by practicing & improving results with e-learning"),

    TOP_COACHING_COURSES_FOR("Popular Courses"),
    TOP_COACHING_INSTITUTES_FOR("Top Institutes"),
    COACHING_FOR("Popular %s Exams"),

    ALL_YOU_NEED_TO_KNOW_ABOUT("All you need to know about %s"),

    TARGET_EXAM_COURSE("Target Exam"),
    ELIGIBILITY_COURSE("Eligibility"),
    DURATION_COURSE("Duration"),
    VALIDITY_COURSE("Validity"),

    CGST_KEY("cgst"),
    CGST_HEADER("Central GST"),
    CGST_OMS_KEY("totalCGST"),
    IGST_KEY("igst"),
    IGST_HEADER("Integrated GST"),
    IGST_OMS_KEY("totalIGST"),
    SGST_KEY("sgst"),
    SGST_HEADER("State GST"),
    SGST_OMS_KEY("totalSGST"),
    UTGST_KEY("utgst"),
    UTGST_HEADER("Union Territory GST"),
    UTGST_OMS_KEY("totalUTGST");

    private String value;
}
