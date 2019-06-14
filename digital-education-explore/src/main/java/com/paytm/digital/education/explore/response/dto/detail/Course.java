package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Course {

    @JsonProperty("course_id")
    private long courseId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("url_display_key")
    private String urlDisplayKey;

    @JsonProperty("duration_in_month")
    private Integer durationInMonth;

    @JsonProperty("seats")
    private Integer seats;

    @JsonProperty("fee")
    private Long fee;
}
