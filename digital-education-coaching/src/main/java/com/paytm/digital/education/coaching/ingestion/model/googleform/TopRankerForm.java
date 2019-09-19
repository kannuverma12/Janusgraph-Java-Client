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
public class TopRankerForm {

    @JsonProperty("top_ranker_id")
    @GoogleSheetColumnName("Top Ranker Id")
    private Long topRankerId;

    @JsonProperty("institute_id")
    @GoogleSheetColumnName("Institute Id")
    private Long instituteId;

    @JsonProperty("center_id")
    @GoogleSheetColumnName("Center Id")
    private Long centerId;

    @JsonProperty("exam_id")
    @GoogleSheetColumnName("Exam Id")
    private Long examId;

    @JsonProperty("student_name")
    @GoogleSheetColumnName("Student Name")
    private String studentName;

    @JsonProperty("student_photo")
    @GoogleSheetColumnName("Student Photo")
    private String studentPhoto;

    @JsonProperty("course_ids")
    @GoogleSheetColumnName("Course Ids")
    private String courseIds;

    @JsonProperty("year_and_batch")
    @GoogleSheetColumnName("Year And Batch")
    private String yearAndBatch;

    @JsonProperty("rank_obtained")
    @GoogleSheetColumnName("Rank Obtained")
    private String rankObtained;

    @JsonProperty("exam_year")
    @GoogleSheetColumnName("Exam Year")
    private String examYear;

    @JsonProperty("college_admitted")
    @GoogleSheetColumnName("College Admitted")
    private String collegeAdmitted;

    @JsonProperty("testimonial")
    @GoogleSheetColumnName("Testimonial")
    private String testimonial;

    @JsonProperty("category")
    @GoogleSheetColumnName("Category")
    private String category;

    @JsonProperty("global_priority")
    @GoogleSheetColumnName("Global Priority")
    private Integer globalPriority;

    @JsonProperty("status_active")
    @GoogleSheetColumnName("Status Active")
    private String statusActive;
}
