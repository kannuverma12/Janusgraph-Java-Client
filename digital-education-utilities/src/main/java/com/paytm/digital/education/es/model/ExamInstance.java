package com.paytm.digital.education.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamInstance {

    @JsonProperty("instance_id")
    private long instanceId;

    @JsonProperty("admission_year")
    private int admissionYear;

    @JsonProperty("events")
    private List<Event> events;

    @JsonProperty("syllabus_available")
    private boolean syllabusAvailable;
}
