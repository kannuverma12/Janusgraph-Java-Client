package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompareInstDetail {

    @JsonProperty("institute_id")
    private Long institute_id;

    @JsonProperty("rankings")
    private List<Ranking> rankings;

    @JsonProperty("approvals")
    private List<String> approvals;

    @JsonProperty("total_intake")
    private Integer totalIntake;

    @JsonProperty("campus_area")
    private String campusArea;

    @JsonProperty("minimum_course_fee") //check logic
    private Long minimumCourseFee;

    @JsonProperty("placements")
    private String placements;

    @JsonProperty("exams_accepted")
    private List<String> examAccepted;

    @JsonProperty("streams_prepared_for")
    private Set<String> streamsPreparedFor;

    @JsonProperty("course_level")
    private Set<String> courseLevel;

    @Field("facilities")
    private List<String> facilities;








}
