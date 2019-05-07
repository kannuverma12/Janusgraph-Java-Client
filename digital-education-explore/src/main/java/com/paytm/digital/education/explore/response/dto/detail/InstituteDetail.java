package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.database.entity.Alumni;
import com.paytm.digital.education.explore.response.dto.common.BannerData;
import com.paytm.digital.education.explore.response.dto.common.OfficialAddress;
import com.paytm.digital.education.explore.response.dto.common.Widget;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstituteDetail {

    @JsonProperty("institute_id")
    private long instituteId;

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("brochure_url")
    private String brochureUrl;

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

    @JsonProperty("get_in_touch")
    private boolean getInTouch;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("courses")
    private List<Course> courses;

    @JsonProperty("total_courses")
    private long totalCourses;

    @JsonProperty("cut_offs")
    private List<ExamAndCutOff> cutOffs;

    @JsonProperty("facilities")
    private List<Facility> facilities;

    @JsonProperty("gallery")
    private Gallery gallery;

    @JsonProperty("placements")
    private List<Placement> placements;

    @JsonProperty("widgets")
    private List<Widget> widgets;

    @JsonProperty("banners")
    private List<BannerData> banners;

    @JsonProperty("sections")
    private List<String> sections;

    @JsonProperty("notable_alumni")
    private List<Alumni> notableAlumni;

    @JsonProperty("rankings")
    private Map<String, List<Ranking>> rankings;

    @JsonProperty("degree_offered")
    private Map<String, Set<String>> degreeOffered;
}
