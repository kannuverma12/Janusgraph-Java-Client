package com.paytm.digital.education.coaching.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.paytm.digital.education.coaching.enums.CourseType;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Course {

    private Long       courseId;
    private Long       instituteId;
    private List<Long> coachingCenters;
    private String     officialName;
    private CourseType courseType;
    private String streamPreparedFor;
    private List<Long> exams;
    private int durationInMonths;
    private String eligibilityCriteria;
    //private String courseDetails;


}
