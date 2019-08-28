package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.database.embedded.CoachingCourseFeature;
import com.paytm.digital.education.database.embedded.Faq;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.enums.Level;
import com.paytm.digital.education.coaching.producer.model.dto.CoachingProgramSessionDetails;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CoachingCourseCreateRequest {

    List<Faq> faqs;

    @NotNull
    @ApiModelProperty(value = "id of the course\n")
    private Long id;

    @NotNull
    @ApiModelProperty(value = "name of the course\n")
    private String name;

    @NotNull
    @JsonProperty("coaching_institute_id")
    private Long coachingInstituteId;

    @NotNull
    @JsonProperty("course_type")
    private CourseType courseType;

    @NotNull
    private Long stream;

    @NotNull
    @JsonProperty("primary_exam_ids")
    private List<Long> primaryExamIds;

    @JsonProperty("auxiliary_exam_ids")
    private List<Long> auxiliaryExams;
    // Duration in Months
    @NotNull
    private String duration;

    @NotNull
    private String eligibility;

    @NotNull
    private String info;

    @NotNull
    private String description;

    @NotNull
    private Double price;

    @NotNull
    private Level level;

    private String language;

    @NotNull
    @JsonProperty("syllabus_and_brochure")
    private String syllabusAndBrochure;

    @NotNull
    @JsonProperty("global_priority")
    private Integer globalPriority;

    @NotNull
    @JsonProperty("session_details")
    private List<CoachingProgramSessionDetails> sessionDetails;

    @NotNull
    @JsonProperty("features")
    private List<CoachingCourseFeature> features;

    @JsonProperty("is_scholarship_available")
    private Boolean isScholarshipAvailable;

    @JsonProperty("test_count")
    private Integer testCount;

    @JsonProperty("test_duration")
    private Integer testDuration;

    @JsonProperty("test_series_duration")
    private Integer testSeriesDuration;

    @JsonProperty("types_of_results")
    private String typesOfResults;

    @JsonProperty("is_doubt_solving_session_available")
    private Boolean isDoubtSolvingSessionAvailable;

    @JsonProperty("number_of_books")
    private Integer numberOfBooks;

    @JsonProperty("delivery_schedule")
    private String deliverySchedule;

    private List<String> inclusions;

    @JsonProperty("how_to_use")
    private List<String> howToUse;

    @NotNull
    @JsonProperty("is_enabled")
    private Boolean isEnabled = Boolean.TRUE;
}
