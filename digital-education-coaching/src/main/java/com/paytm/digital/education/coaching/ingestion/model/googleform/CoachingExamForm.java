package com.paytm.digital.education.coaching.ingestion.model.googleform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.coaching.ingestion.model.GoogleSheetColumnName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoachingExamForm {

    @JsonProperty("coaching_exam_id")
    @GoogleSheetColumnName("Coaching Exam Id")
    private Long coachingExamId;

    @JsonProperty("institute_id")
    @GoogleSheetColumnName("Institute Id")
    private Long instituteId;

    @JsonProperty("exam_type")
    @GoogleSheetColumnName("Exam Type")
    private String examType;

    @JsonProperty("exam_name")
    @GoogleSheetColumnName("Exam Name")
    private String examName;

    @JsonProperty("exam_description")
    @GoogleSheetColumnName("Exam Description")
    private String examDescription;

    @JsonProperty("stream_ids")
    @GoogleSheetColumnName("Stream Ids")
    private String streamIds;

    @JsonProperty("course_ids")
    @GoogleSheetColumnName("Course Ids")
    private String courseIds;

    @JsonProperty("exam_duration")
    @GoogleSheetColumnName("Exam Duration")
    private String examDuration;

    @JsonProperty("maximum_marks")
    @GoogleSheetColumnName("Maximum Marks")
    private Double maximumMarks;

    @JsonProperty("exam_dates")
    @GoogleSheetColumnName("Exam Dates")
    private String examDates;

    @JsonProperty("eligibility")
    @GoogleSheetColumnName("Eligibility")
    private String eligibility;

    @JsonProperty("number_of_questions")
    @GoogleSheetColumnName("Number Of Questions")
    private String numberOfQuestions;

    @JsonProperty("global_priority")
    @GoogleSheetColumnName("Global Priority")
    private Integer globalPriority;

    @JsonProperty("status_active")
    @GoogleSheetColumnName("Status Active")
    private String statusActive;
}
