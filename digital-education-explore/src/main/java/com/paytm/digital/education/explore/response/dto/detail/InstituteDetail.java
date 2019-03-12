package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.response.dto.common.OfficialAddress;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstituteDetail {

    @Field("institute_id")
    @JsonProperty("institute_id")
    private long instituteId;

    @Field("official_name")
    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("official_address")
    private OfficialAddress officialAddress;

    @JsonProperty("institute_type")
    private String instituteType;

    @JsonProperty("established_year")
    private Integer establishedYear;

    @JsonProperty("derived_attributes")
    private Map<String, List<Attribute>> derivedAttributes;

    @JsonProperty("shortlisted")
    private boolean shortlisted;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("courses")
    private List<Course> courses;

    @JsonProperty("rankings")
    private List<Ranking> rankings;

    @JsonProperty("cut_off")
    private List<CutOff> cutOff;

    @JsonProperty("facilities")
    private List<Facility> facilities;

    @JsonProperty("gallery")
    private Gallery gallery;

    @JsonProperty("placements")
    private List<Placement> placements;
}
