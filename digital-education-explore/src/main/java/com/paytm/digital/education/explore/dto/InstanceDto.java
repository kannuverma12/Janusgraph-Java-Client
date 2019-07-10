package com.paytm.digital.education.explore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstanceDto {

    @JsonProperty("admission_year")
    private Integer admissionYear;

    @JsonProperty("exam_centers")
    private List<String> examCenters;

    @JsonProperty("events")
    private List<EventDto> events;

    @JsonProperty("instance_id")
    private Integer instanceId;

    @JsonProperty("name")
    private String instanceName;

    @JsonProperty("parent_instance_id")
    private Integer parentInstanceId;

    @JsonProperty("eligibility_criteria")
    private String eligibilityCriteria;

    @JsonProperty("syllabus")
    private List<SyllabusDto> syllabusList;

    @JsonProperty("pattern")
    private String pattern;

    @JsonProperty("admit_card_download_url")
    private String admitCardDownloadUrl;

    @JsonProperty("application_process_url_official")
    private String applicationProcessUrlOfficial;

    @JsonProperty("eligibility_requirements_url_official")
    private String eligibilityRequirementsUrlOfficial;

    @JsonProperty("registration_url")
    private String registrationUrl;

    @JsonProperty("results_url")
    private String resultUrl;

    @JsonProperty("question_paper_urls")
    private List<String> questionPaperUrls;
}
