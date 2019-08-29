package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.enums.DurationType;
import com.paytm.digital.education.enums.Language;
import com.paytm.digital.education.enums.Level;
import com.paytm.digital.education.validator.PositiveElementsCollection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingCourseDataRequest {

    @Min(value = 1)
    @ApiModelProperty(value = "unique existing course id, should be ignored in case of new record")
    private Long courseId;

    @NotEmpty
    @Size(max = 50)
    @ApiModelProperty(value = "name of course")
    private String name;

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(value = "id of the coaching institute")
    private Long instituteId;

    @NotNull
    @ApiModelProperty(value = "course type from predefined course types")
    private CourseType courseType;

    @NotEmpty
    @PositiveElementsCollection
    @ApiModelProperty(value = "id of existing streams")
    private List<Long> streamIds;

    @NotEmpty
    @PositiveElementsCollection
    @ApiModelProperty(value = "id of primary exams")
    private List<Long> primaryExamIds;

    @NotEmpty
    @PositiveElementsCollection
    @ApiModelProperty(value = "id of auxiliary exams")
    private List<Long> auxiliaryExams;

    @NotNull
    @ApiModelProperty(value = "duration type from predefined duration types")
    private DurationType durationType;

    @NotNull
    @Positive
    @ApiModelProperty(value = "course duration")
    private Integer duration;

    @NotEmpty
    @Size(max = 50)
    @ApiModelProperty(value = "eligibility for coaching course")
    private String eligibility;

    @NotEmpty
    @Size(max = 50)
    @ApiModelProperty(value = "short description about coaching course")
    private String info;

    @NotEmpty
    @Size(max = 500)
    @ApiModelProperty(value = "description about coaching course")
    private String description;

    @NotEmpty
    @Max(value = 200000)
    @Min(value = 1)
    @ApiModelProperty(value = "mrp of for coaching course")
    private Double price;

    @NotNull
    @ApiModelProperty(value = "existing education level of applicant")
    private Level level;

    @NotNull
    @ApiModelProperty(value = "language of coaching course")
    private Language language;

    @URL
    @NotEmpty
    private String syllabusAndBrochure;

    @Min(value = 0)
    @ApiModelProperty(value = "priority of coaching course across existing courses")
    private Integer priority = new Integer(0);

    @NotNull
    @ApiModelProperty(value = "certified after course completion")
    private Boolean isCertificateAvailable;

    @NotNull
    @ApiModelProperty(value = "doubt solving / discussion forum facility availability")
    private Boolean isDoubtSolvingForumAvailable;

    @NotNull
    @ApiModelProperty(value = "test analysis / performance tracker facility availability")
    private Boolean isProgressAnalysisAvailable;

    @NotNull
    @ApiModelProperty(value = "all India rank / analysis facility availability")
    private Boolean isRankAnalysisAvailable;

    @NotEmpty
    @ApiModelProperty(value = "course feature ids provided at institute level")
    private List<Long> courseFeatureIds;


    @Positive
    @Max(value = 100)
    @ApiModelProperty(value = "number of test counts for test series")
    private Integer testCount;

    @Positive
    @Max(value = 300)
    @ApiModelProperty(value = "duration of each test in minutes test series")
    private Integer testDuration;

    @Positive
    @Max(value = 300)
    @ApiModelProperty(value = "question count in each test of test series")
    private Integer testQuestionCount;

    @Positive
    @Max(value = 50)
    @ApiModelProperty(value = "practice paper count of test series")
    private Integer testPracticePaperCount;


    @Positive
    @Max(value = 100)
    @ApiModelProperty(value = "provided books count in the course")
    private Integer distanceLearningBooksCount;

    @Positive
    @Max(value = 100)
    @ApiModelProperty(value = "provided solved paper count in the course")
    private Integer distanceLearningSolvedPaperCount;

    @Positive
    @Max(value = 100)
    @ApiModelProperty(value = "provided assignments count in the course")
    private Integer distanceLearningAssignmentsCount;


    @Positive
    @Max(value = 100)
    @ApiModelProperty(value = "provided lecture count in the course")
    private Integer elearningLectureCount;

    @Positive
    @Max(value = 300)
    @ApiModelProperty(value = "duration of each lecture in minutes in e-learning course")
    private Integer elearningLectureDuration;

    @Positive
    @Max(value = 100)
    @ApiModelProperty(value = "online test count in the course")
    private Integer elearningOnlineTestCount;

    @Positive
    @Max(value = 100)
    @ApiModelProperty(value = "practice paper count in the course")
    private Integer elearningPracticePaperCount;


    @Positive
    @Max(value = 100)
    @ApiModelProperty(value = "provided lecture count in the course")
    private Integer classroomLectureCount;

    @Positive
    @Max(value = 300)
    @ApiModelProperty(value = "duration of each lecture in the course")
    private Integer classroomLectureDuration;

    @Positive
    @Max(value = 100)
    @ApiModelProperty(value = "online test count in the course")
    private Integer classroomTestCount;

    @Positive
    @Max(value = 100)
    @ApiModelProperty(value = "practice paper count in the course")
    private Integer classroomTeacherStudentRatio;

    private String howToUse1;

    private String howToUse2;

    private String howToUse3;

    private String howToUse4;

    @ApiModelProperty(value = "flag is enable/disable course, default is true")
    private Boolean isEnabled = Boolean.TRUE;
}
