package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.response.dto.common.BannerData;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.common.OfficialAddress;
import com.paytm.digital.education.explore.response.dto.common.Widget;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.paytm.digital.education.explore.enums.EducationEntity.INSTITUTE;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstituteDetail implements CTAInfoHolder {

    @JsonProperty("institute_id")
    private long instituteId;

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("common_name")
    private String commonName;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("brochure_url")
    private String brochureUrl;

    @JsonProperty("url_display_key")
    private String urlDisplayName;

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

    @JsonProperty("interested")
    private boolean interested;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("courses")
    private List<Course> courses;

    @JsonProperty("courses_per_degree")
    private Map<String, List<Course>> coursesPerLevel;

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

    @JsonProperty("banner1")
    private BannerData banner1;

    @JsonProperty("banner2")
    private BannerData banner2;

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

    @JsonProperty("is_client")
    private boolean client;

    @JsonProperty("campus_ambassadors")
    private List<Ambassador> campusAmbassadors;

    @JsonProperty("articles")
    private List<CampusArticle> articles;

    @JsonProperty("events")
    private List<CampusEventDetail> events;

    @JsonProperty("cta_list")
    private List<CTA> ctaList;

    @JsonIgnore
    private Long pid;

    @JsonIgnore
    private Long mid;

    @JsonIgnore
    @Accessors(fluent = true)
    private boolean shouldHaveLeadCTA = true;

    @JsonIgnore
    @Accessors(fluent = true)
    private boolean shouldHaveApplyNowCTA = false;

    @JsonIgnore
    @Override
    public boolean hasShareFeature() {
        return false;
    }

    @Override
    @JsonIgnore
    public EducationEntity getCorrespondingEntity() {
        return INSTITUTE;
    }

}
