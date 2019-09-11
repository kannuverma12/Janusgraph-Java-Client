package com.paytm.digital.education.coaching.producer.model.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.enums.DurationType;
import com.paytm.digital.education.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingCourseDTO {

    private Long courseId;

    private String name;

    private Long instituteId;

    private CourseType courseType;

    private List<Long> streamIds;

    private List<Long> primaryExamIds;

    private List<Long> auxiliaryExams;

    private DurationType durationType;

    private Integer duration;

    private String eligibility;

    private String info;

    private String description;

    private Double price;

    private CourseLevel level;

    private Language language;

    private String syllabusAndBrochure;

    private Integer priority;

    private Boolean isCertificateAvailable;

    private Boolean isDoubtSolvingForumAvailable;

    private Boolean isProgressAnalysisAvailable;

    private Boolean isRankAnalysisAvailable;

    private List<Long> courseFeatureIds;

    private Boolean isEnabled;
}
