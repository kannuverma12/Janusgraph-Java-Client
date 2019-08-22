package com.paytm.digital.education.coaching.database.entity;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.YYYY_MM_DD_T_HH_MM_SS;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.coaching.response.dto.ResponseDto;
import lombok.Data;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@Document("coaching_course")
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoachingCourse extends ResponseDto {

    @Field("_id")
    @JsonIgnore
    ObjectId id;

    @Field("course_id")
    @JsonProperty("course_id")
    private Long courseId;

    @Field("institute_id")
    @JsonProperty("institute_id")
    private Long instituteId;

    @Field("coaching_centers")
    @JsonProperty("coaching_centers")
    private List<Long> coachingCenters;

    @Field("course_name")
    @JsonProperty("course_name")
    private String courseName;

    @Field("course_type")
    @JsonProperty("course_type")
    private CourseType courseType;

    @Field("stream_prepared_for")
    @JsonProperty("stream_prepared_for")
    private List<String> streamPreparedFor;

    @Field("exams")
    @JsonProperty("exams")
    private List<Long> exams;

    @Field("course_duration_in_months")
    @JsonProperty("course_duration_in_months")
    private Integer courseDurationInMonths;

    @Field("eligibility_criteria")
    @JsonProperty("eligibility_criteria")
    private String eligibilityCriteria; //TODO check the structure

    @Field("course_description")
    @JsonProperty("course_description")
    private String courseDescription;

    @Field("course_details")
    @JsonProperty("course_details")
    private String courseDetails; //TODO define the course detail structure

    @Field("class_schedule")
    @JsonProperty("class_schedule")
    private String classSchedule;//TODO structure

    @Field("study_material_description")
    @JsonProperty("study_material_description")
    private String studyMaterialDescription;

    @Field("teaching_methodology")
    @JsonProperty("teaching_methodology")
    private String teachingMethodology;

    @Field("success_stories")
    @JsonProperty("success_stories")
    private List<SuccessStory> successStories;

    @Field("facilities")
    @JsonProperty("facilities")
    private List<String> facilities;

    @JsonProperty("active")
    @Field("active")
    private Boolean active = true;

    @Field("fees")
    @JsonProperty("fees")
    private FeeStructure feeStructure;

    @Field("scholarship_exams")
    @JsonProperty("scholarship_exams")
    private List<Long> scholarshipExam;

    @Field("course_benefits")
    @JsonProperty("course_benefits")
    private String courseBenefits;

    @Field("sample_resource")
    @JsonProperty("sample_resource")
    private SampleResource sampleResource;

    @Field("course_structure")
    @JsonProperty("course_structure")
    private String courseStructure; //TODO define the structure

    @Field("certification_available")
    @JsonProperty("certification_available")
    private Boolean certificationAvailable;

    @Field("created_at")
    @JsonProperty("created_at")
    @JsonFormat(pattern = YYYY_MM_DD_T_HH_MM_SS)
    private Date createdAt;

    @Field("updated_at")
    @JsonProperty("updated_at")
    @JsonFormat(pattern = YYYY_MM_DD_T_HH_MM_SS)
    private Date updatedAt;

}
