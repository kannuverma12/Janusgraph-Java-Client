package com.paytm.digital.education.coaching.ingestion.model.googleform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.coaching.ingestion.model.GoogleSheetColumnName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoachingCourseForm {

    @JsonProperty("course_id")
    @GoogleSheetColumnName("Course Id")
    private Long courseId;

    @JsonProperty("course_name")
    @GoogleSheetColumnName("Course Name")
    private String courseName;

    @JsonProperty("institute_id")
    @GoogleSheetColumnName("Institute Id")
    private Long instituteId;

    @JsonProperty("course_type")
    @GoogleSheetColumnName("Course Type")
    private String courseType;

    @JsonProperty("stream_ids")
    @GoogleSheetColumnName("Stream Ids")
    private String streamIds;

    @JsonProperty("exam_prepared_ids")
    @GoogleSheetColumnName("Exam Prepared Ids")
    private String examPreparedIds;

    @JsonProperty("course_duration_value")
    @GoogleSheetColumnName("Course Duration Value")
    private Integer courseDurationValue;

    @JsonProperty("course_duration_type")
    @GoogleSheetColumnName("Course Duration Type")
    private String courseDurationType;

    @JsonProperty("course_validity_value")
    @GoogleSheetColumnName("Course Validity Value")
    private Integer courseValidityValue;

    @JsonProperty("course_validity_type")
    @GoogleSheetColumnName("Course Validity Type")
    private String courseValidityType;

    @JsonProperty("eligibility_criteria")
    @GoogleSheetColumnName("Eligibility Criteria")
    private String eligibilityCriteria;

    @JsonProperty("course_introduction")
    @GoogleSheetColumnName("Course Introduction")
    private String courseIntroduction;

    @JsonProperty("course_description")
    @GoogleSheetColumnName("Course Description")
    private String courseDescription;

    @JsonProperty("feature_ids")
    @GoogleSheetColumnName("Feature Ids")
    private String featureIds;

    @JsonProperty("original_price")
    @GoogleSheetColumnName("Original Price")
    private Double originalPrice;

    @JsonProperty("discounted_price")
    @GoogleSheetColumnName("Discounted Price")
    private Double discountedPrice;

    @JsonProperty("level_of_education")
    @GoogleSheetColumnName("Level Of Education")
    private String levelOfEducation;

    @JsonProperty("language")
    @GoogleSheetColumnName("Language")
    private String language;

    @JsonProperty("syllabus")
    @GoogleSheetColumnName("Syllabus")
    private String syllabus;

    @JsonProperty("course_covers")
    @GoogleSheetColumnName("Course Covers")
    private String courseCovers;

    @JsonProperty("how_to_use1")
    @GoogleSheetColumnName("How To Use1")
    private String howToUse1;

    @JsonProperty("how_to_use2")
    @GoogleSheetColumnName("How To Use2")
    private String howToUse2;

    @JsonProperty("how_to_use3")
    @GoogleSheetColumnName("How To Use3")
    private String howToUse3;

    @JsonProperty("how_to_use4")
    @GoogleSheetColumnName("How To Use4")
    private String howToUse4;

    @JsonProperty("important_date_key1")
    @GoogleSheetColumnName("Important Date Key1")
    private String importantDateKey1;

    @JsonProperty("important_date_val1")
    @GoogleSheetColumnName("Important Date Val1")
    private String importantDateVal1;

    @JsonProperty("important_date_key2")
    @GoogleSheetColumnName("Important Date Key2")
    private String importantDateKey2;

    @JsonProperty("important_date_val2")
    @GoogleSheetColumnName("Important Date Val2")
    private String importantDateVal2;

    @JsonProperty("important_date_key3")
    @GoogleSheetColumnName("Important Date Key3")
    private String importantDateKey3;

    @JsonProperty("important_date_val3")
    @GoogleSheetColumnName("Important Date Val3")
    private String importantDateVal3;

    @JsonProperty("certificate")
    @GoogleSheetColumnName("Certificate")
    private String certificate;

    @JsonProperty("doubt_solving_session_available")
    @GoogleSheetColumnName("Doubt Solving Session Available")
    private String doubtSolvingSessionAvailable;

    @JsonProperty("test_analysis_and_comparison_report_available")
    @GoogleSheetColumnName("Test Analysis And Comparison Report Available")
    private String testAnalysisAndComparisonReportAvailable;

    @JsonProperty("all_india_rank_available")
    @GoogleSheetColumnName("All India Rank Available")
    private String allIndiaRankAvailable;

    @JsonProperty("number_of_tests_in_test_series")
    @GoogleSheetColumnName("Number Of Tests In Test Series")
    private Integer numberOfTestsInTestSeries;

    @JsonProperty("duration_of_each_test_in_test_series")
    @GoogleSheetColumnName("Duration Of Each Test In Test Series")
    private Integer durationOfEachTestInTestSeries;

    @JsonProperty("number_of_questions_in_test_series")
    @GoogleSheetColumnName("Number Of Questions In Test Series")
    private Integer numberOfQuestionsInTestSeries;

    @JsonProperty("number_of_daily_practice_tests_available_in_test_series")
    @GoogleSheetColumnName("Number Of Daily Practice Tests Available In Test Series")
    private Integer numberOfDailyPracticeTestsAvailableInTestSeries;

    @JsonProperty("number_of_books")
    @GoogleSheetColumnName("Number Of Books")
    private Integer numberOfBooks;

    @JsonProperty("number_of_solved_papers")
    @GoogleSheetColumnName("Number Of Solved Papers")
    private Integer numberOfSolvedPapers;

    @JsonProperty("number_of_assignments")
    @GoogleSheetColumnName("Number Of Assignments")
    private Integer numberOfAssignments;

    @JsonProperty("number_of_live_lectures")
    @GoogleSheetColumnName("Number Of Live Lectures")
    private Integer numberOfLiveLectures;

    @JsonProperty("duration_of_each_live_lecture")
    @GoogleSheetColumnName("Duration Of Each Live Lecture")
    private Integer durationOfEachLiveLecture;

    @JsonProperty("number_of_tests_available_online")
    @GoogleSheetColumnName("Number Of Tests Available Online")
    private Integer numberOfTestsAvailableOnline;

    @JsonProperty("number_of_daily_practice_paper_available_online")
    @GoogleSheetColumnName("Number Of Daily Practice Paper Available Online")
    private Integer numberOfDailyPracticePaperAvailableOnline;

    @JsonProperty("number_of_classroom_lectures")
    @GoogleSheetColumnName("Number Of Classroom Lectures")
    private Integer numberOfClassroomLectures;

    @JsonProperty("duration_of_classroom_lectures")
    @GoogleSheetColumnName("Duration Of Classroom Lectures")
    private Integer durationOfClassroomLectures;

    @JsonProperty("number_of_classroom_tests")
    @GoogleSheetColumnName("Number Of Classroom Tests")
    private Integer numberOfClassroomTests;

    @JsonProperty("teacherToStudentRatio")
    @GoogleSheetColumnName("Teacher To Student Ratio")
    private String teacherToStudentRatio;

    @JsonProperty("sgst")
    @GoogleSheetColumnName("SGST")
    private String sgst;

    @JsonProperty("cgst")
    @GoogleSheetColumnName("CGST")
    private String cgst;

    @JsonProperty("igst")
    @GoogleSheetColumnName("IGST")
    private String igst;

    @JsonProperty("tcs")
    @GoogleSheetColumnName("TCS")
    private String tcs;

    @JsonProperty("merchant_product_id")
    @GoogleSheetColumnName("Merchant Product Id")
    private String merchantProductId;

    @JsonProperty("global_priority")
    @GoogleSheetColumnName("Global Priority")
    private Integer globalPriority;

    @JsonProperty("status_active")
    @GoogleSheetColumnName("Status Active")
    private String statusActive;

    @JsonProperty("paytm_product_id")
    @GoogleSheetColumnName("Paytm Product Id")
    private Long paytmProductId;

    @JsonProperty("is_dynamic")
    @GoogleSheetColumnName("Is Dynamic")
    private String isDynamic;

    @JsonProperty("redirect_url")
    @GoogleSheetColumnName("Redirect Url")
    private String redirectUrl;

    @JsonProperty("post_order_cta")
    @GoogleSheetColumnName("Post order CTA")
    private String postOrderCta;

    @JsonProperty("list_page_cta")
    @GoogleSheetColumnName("List Page CTA")
    private String listPageCta;

    @JsonProperty("details_page_cta")
    @GoogleSheetColumnName("Details Page CTA")
    private String detailsPageCta;

}
