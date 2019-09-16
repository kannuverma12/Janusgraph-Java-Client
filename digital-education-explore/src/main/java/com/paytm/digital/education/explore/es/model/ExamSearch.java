package com.paytm.digital.education.explore.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamSearch {

    @JsonProperty("exam_id")
    private int                examId;

    @JsonProperty("official_name")
    private String             officialName;

    @JsonProperty("names")
    private List<String>       names;

    @JsonProperty("image_link")
    private String             imageLink;

    @JsonProperty("linguistic_medium")
    private List<String>       linguisticMedium;

    @JsonProperty("level")
    private String             level;

    @JsonProperty("instances")
    private List<ExamInstance> examInstances;

    @JsonProperty("tabs_available")
    private List<String>       dataAvailable;

    @JsonProperty("domain_name")
    private List<String> domainName;
}
