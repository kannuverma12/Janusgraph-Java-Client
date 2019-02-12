package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Instance {

    @Field("admissionYear")
    @JsonProperty("admission_year")
    private Integer admissionYear;

    @Field("admitCardDownloadUrl")
    @JsonProperty("admit_card_download_url")
    private String admitCardDownloadUrl;

    @Field("exam_centers")
    @JsonProperty("exam_centers")
    private List<String> examCenters;

    @Field("eligibilityRequirementsUrlOfficial")
    @JsonProperty("eligibility_requirements_url_official")
    private String eligibilityRequirementsUrlOfficial;

    @Field("events")
    @JsonProperty("events")
    private List<Event> events;

    @Field("instanceId")
    @JsonProperty("instance_id")
    private Integer instanceId;

    @Field("parentInstanceId")
    @JsonProperty("parent_instance_id")
    private Integer parentInstanceId;

    @Field("instance_name")
    @JsonProperty("instance_name")
    private String instanceName;

    @Field("registrationUrl")
    @JsonProperty("registration_url")
    private String registrationUrl;

    @Field("resultsUrl")
    @JsonProperty("results_url")
    private String resultsUrl;
}
