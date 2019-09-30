package com.paytm.digital.education.coaching.consumer.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TopRanker {

    private long         id;
    private long         coachingInstituteId;
    private long         coachingCentreId;
    private List<String> coachingCourseNames;
    private String       examName;
    private String       studentName;
    private String       image;
    private String       rank;
    private String       examYear;
    private String       testimonial;
    private String       centerCity;
}
