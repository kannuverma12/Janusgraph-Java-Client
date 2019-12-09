package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Instance {

    @Field("admission_year")
    private Integer admissionYear;

    @Field("exam_centers")
    private List<String> examCenters;

    @Field("events")
    private List<Event> events;

    @Field("instance_id")
    private Integer instanceId;

    @Field("name")
    private String instanceName;

    @Field("parent_instance_id")
    @JsonProperty("parent_instance_id")
    private Integer parentInstanceId;

    @Field("eligibility_criteria")
    private String eligibilityCriteria;

    @Field("syllabus")
    private List<Syllabus> syllabusList;

    @Field("pattern")
    private String pattern;

    @Field("admit_card_download_url")
    private String admitCardDownloadUrl;

    @Field("application_process_url_official")
    private String applicationProcessUrlOfficial;

    @Field("eligibility_requirements_url_official")
    private String eligibilityRequirementsUrlOfficial;

    @Field("registration_url")
    private String registrationUrl;

    @Field("question_paper_urls")
    private List<String> questionPaperUrls;

    @Field("results_url")
    private String resultUrl;

    private String examName;
}
