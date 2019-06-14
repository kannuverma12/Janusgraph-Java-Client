package com.paytm.digital.education.explore.response.dto.search;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseData {

    @JsonProperty("course_id")
    private Long         courseId;

    @JsonProperty("official_name")
    private String       officialName;

    @JsonProperty("url_display_key")
    private String       urlDisplayKey;

    @JsonProperty("duration_in_months")
    private Integer      durationInMonths;

    @JsonProperty("fee")
    private Long         fee;

    @JsonProperty("stream")
    private List<String> stream;

    @JsonProperty("seats_available")
    private Integer      seatsAvailable;

    @JsonProperty("degrees")
    private List<String> degrees;

    @JsonProperty("college_name")
    private String       instituteName;

}
