package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.database.entity.Placement;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties( { "fake_rankings", "fake_placements" })
public class CompareInstDetail {

    @JsonProperty("institute_id")
    private Long instituteId;

    @JsonProperty("fake_rankings")
    private Map<String, Ranking> fakeRankings;

    @JsonProperty("fake_placements")
    private Map<String, Placement> fakePlacements;

    @JsonProperty("approvals")
    private List<String> approvals;

    @JsonProperty("total_intake")
    private Integer totalIntake;

    @JsonProperty("campus_area")
    private String campusArea;

    @JsonProperty("minimum_course_fee")
    private Long minimumCourseFee;

    @JsonProperty("placement")
    private Placement placement;

    @JsonProperty("exams_accepted")
    private List<String> examsAccepted;

    @JsonProperty("streams_prepared_for")
    private Set<String> streamsPreparedFor;

    @JsonProperty("course_level")
    private Set<String> courseLevel;

    @Field("facilities")
    private Map<String, String> facilities;

    @JsonProperty("rankings")
    private Set<CompareRanking> rankings;

}
