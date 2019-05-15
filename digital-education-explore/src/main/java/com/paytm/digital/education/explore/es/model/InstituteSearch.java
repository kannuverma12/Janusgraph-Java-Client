package com.paytm.digital.education.explore.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstituteSearch {

    @JsonProperty("institute_id")
    private long instituteId;

    @JsonProperty("parent_institute_id")
    private long parentInstituteId;

    @JsonProperty("university_name")
    private String universityName;

    @JsonProperty("institute_type")
    private String instituteType;

    @JsonProperty("approved_by")
    private List<String> approvedBy;

    @JsonProperty("accredited_to")
    private List<String> accreditedTo;

    @JsonProperty("image_link")
    private String imageLink;

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("names")
    private List<String> names;

    @JsonProperty("ownership")
    private String ownership;

    @JsonProperty("state")
    private String state;

    @JsonProperty("city")
    private String city;

    @JsonProperty("year_of_estd")
    private int establishedYear;

    @JsonProperty("institute_gender")
    private List<String> instituteGender;

    @JsonProperty("facilities")
    private List<String> facilities;

    @JsonProperty("exams_accepted")
    private List<String> examsAccepted;

    @JsonProperty("courses_offered")
    private List<String> coursesOffered;

    @JsonProperty("max_rating")
    private float maxRating;

    @JsonProperty("max_rank")
    private long maxRank;

    @JsonProperty("is_client")
    private boolean isClient;

    @JsonProperty("courses")
    private List<NestedCourseSearch> courses;
}
