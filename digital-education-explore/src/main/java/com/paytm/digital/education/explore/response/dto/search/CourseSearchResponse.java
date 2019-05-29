package com.paytm.digital.education.explore.response.dto.search;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.response.dto.common.OfficialAddress;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseSearchResponse extends SearchBaseData {

    @JsonProperty("institute_name")
    private String instituteName;

    @JsonProperty("institute_id")
    private Long instituteId;

    @JsonProperty("url_display_name")
    private String urlDisplayName;

    @JsonProperty("courses")
    private List<CourseData> courses;

    @JsonProperty("official_address")
    private OfficialAddress officialAddress;

}
