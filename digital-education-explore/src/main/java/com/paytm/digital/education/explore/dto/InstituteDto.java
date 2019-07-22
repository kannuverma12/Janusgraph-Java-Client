package com.paytm.digital.education.explore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.database.entity.OfficialAddress;
import com.paytm.digital.education.explore.database.entity.Gallery;
import com.paytm.digital.education.explore.database.entity.Alumni;
import com.paytm.digital.education.explore.database.entity.Accreditation;
import com.paytm.digital.education.explore.database.entity.Placement;
import com.paytm.digital.education.explore.enums.CollegeEntityType;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@ToString
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstituteDto {

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("official_address")
    private OfficialAddress officialAddress;  //make dto

    @JsonProperty("established_year")
    private Integer establishedYear;

    @JsonProperty("salaries_placement")
    private List<Placement> salariesPlacement; //make dto

    @JsonProperty("total_enrollments")
    private Integer totalEnrollments;

    @JsonProperty("approvals")
    private List<String> approvals;

    @JsonProperty("institute_types")
    private List<String> instituteTypes;

    @JsonProperty("id")
    private String instituteId;

    @JsonProperty("common_name")
    private String commonName;

    @JsonProperty("gallery")
    private Gallery gallery; //make dto

    @JsonProperty("last_updated")
    private Date   lastUpdated;

    @JsonProperty("publishing_status")
    private String publishingStatus;

    @JsonProperty("notable_alumni")
    private List<Alumni> notableAlumni;

    @JsonProperty("institution_city")
    private String institutionCity;

    @JsonProperty("companies_placement")
    private List<PlacementDto> companiesPlacement;

    @JsonProperty("url")
    private String url;

    @JsonProperty("genders_accepted")
    private List<String> gendersAccepted;

    @JsonProperty("institution_state")
    private String institutionState;

    @JsonProperty("rankings")
    private List<RankingDto> rankings;

    @JsonProperty("entity_type")
    private CollegeEntityType entityType; //make dto

    @JsonProperty("campus_size")
    private Integer campusSize;

    @JsonProperty("ownership")
    private String ownership;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("faculty_count")
    private Integer facultyCount;

    @JsonProperty("total_intake")
    private Integer totalIntake;

    @JsonProperty("is_client")
    private int isClient;

    @JsonProperty("facilities")
    private List<String> facilities;

    @JsonProperty("status")
    private String status;

    @JsonProperty("official_url_brochure")
    private String officialUrlBrochure;

    @JsonProperty("admission_phone")
    private String admissionPhone;

    @JsonProperty("alternate_names")
    private List<String> alternateNames;

    @JsonProperty("parent_institution")
    private Long parentInstitution;

    @JsonProperty("accreditations")
    private List<Accreditation> accreditations;

    @JsonProperty("former_name")
    private List<String> formerName;

}
