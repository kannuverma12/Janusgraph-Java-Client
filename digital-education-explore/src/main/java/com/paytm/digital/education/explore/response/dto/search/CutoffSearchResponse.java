package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.explore.response.dto.common.CutOff;

import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CutoffSearchResponse {

    @JsonProperty("course_id")
    private Long courseId;

    @JsonProperty("url_display_key")
    private String urlDisplayKey;

    @JsonProperty("course_name_official")
    private String courseNameOfficial;

    @JsonProperty("master_branch")
    private String masterBranch;

    @JsonProperty("course_level")
    private CourseLevel courseLevel;

    @JsonProperty("course_duration")
    private Integer courseDuration;

    @JsonProperty("cutoffs")
    private List<CutOff> cutOffs;

}
