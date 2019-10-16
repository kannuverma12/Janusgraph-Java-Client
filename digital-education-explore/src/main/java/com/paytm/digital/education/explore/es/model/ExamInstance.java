package com.paytm.digital.education.explore.es.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamInstance {

    @JsonProperty("instance_id")
    private long        instanceId;

    @JsonProperty("admission_year")
    private int         admissionYear;

    @JsonProperty("events")
    private List<Event> events;

    @JsonProperty("syllabus_available")
    private boolean     syllabusAvailable;

}
