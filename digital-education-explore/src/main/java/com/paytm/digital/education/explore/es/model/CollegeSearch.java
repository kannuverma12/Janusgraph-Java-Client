package com.paytm.digital.education.explore.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollegeSearch {

    @JsonProperty("institute_id")
    private long            instituteId;

    @JsonProperty("parent_institute_id")
    private long            parentInstituteId;

    @JsonProperty("university_name")
    private String          universityName;

    @JsonProperty("institute_type")
    private List<String>    instituteType;

    @JsonProperty("approved_by")
    private String          approvedBy;

    @JsonProperty("accredited_to")
    private String          accreditedTo;

    @JsonProperty("image_link")
    private String          imageLink;

    @JsonProperty("official_name")
    private String          officialName;

    @JsonProperty("names")
    private List<String>    names;

    @JsonProperty("ownership")
    private String          ownership;

    @JsonProperty("state")
    private String          state;

    @JsonProperty("city")
    private String          city;

    @JsonProperty("year_of_estd")
    private int             establishedYear;

    @JsonProperty("college_type")
    private String          collegeType;

    @JsonProperty("college_gender")
    private String          collegeGender;

    @JsonProperty("facilities")
    private List<String> facilities;

    @JsonProperty("exams_accepted")
    private List<String>    examsAccepted;

    @JsonProperty("courses_offered")
    private List<String>    coursesOffered;

    @JsonProperty("max_rating")
    private float           maxRating;

    @JsonProperty("ranking")
    private long            ranking;

    @JsonProperty("rating")
    private float           rating;

    @JsonProperty("courses")
    private List<CourseSearch> courses;
}
