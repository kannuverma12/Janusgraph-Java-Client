package com.paytm.digital.education.explore.database.ingestion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Instance {

    @Field("admission_year")
    @JsonProperty("admission_year")
    private Integer admissionYear;

    @Field("exam_centers")
    @JsonProperty("exam_centers")
    private List<String> examCenters;

    @Field("events")
    @JsonProperty("events")
    private List<Event> events;

    @Field("instance_id")
    @JsonProperty("instance_id")
    private Integer instanceId;

    @Field("name")
    @JsonProperty("name")
    private String instanceName;

    @Field("parent_instance_id")
    @JsonProperty("parent_instance_id")
    private Integer parentInstanceId;

    @Field("eligibility_criteria")
    @JsonProperty("eligibility_criteria")
    private String eligibilityCriteria;

    @Field("syllabus")
    @JsonProperty("syllabus")
    private List<Syllabus> syllabusList;

    @Field("pattern")
    @JsonProperty("pattern")
    private String pattern;

    @Field("admit_card_download_url")
    @JsonProperty("admit_card_download_url")
    private String admitCardDownloadUrl;

    @Field("application_process_url_official")
    @JsonProperty("application_process_url_official")
    private String applicationProcessUrlOfficial;

    @Field("eligibility_requirements_url_official")
    @JsonProperty("eligibility_requirements_url_official")
    private String eligibilityRequirementsUrlOfficial;

    @Field("registration_url")
    @JsonProperty("registration_url")
    private String registrationUrl;

    @JsonProperty("results_url")
    @Field("results_url")
    private String resultUrl;

    @Field("question_paper_urls")
    @JsonProperty("question_paper_urls")
    private List<String> questionPaperUrls;
}
