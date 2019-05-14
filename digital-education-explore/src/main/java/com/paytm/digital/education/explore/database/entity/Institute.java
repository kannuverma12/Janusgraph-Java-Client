package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.CollegeEntityType;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@ToString
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document
public class Institute {

    @Field("_id")
    @JsonIgnore
    private String id;

    @Field("institute_id")
    @JsonProperty("institute_id")
    private Long instituteId;

    @Field("parent_institution")
    private Long parentInstitution;

    @Field("admission_phone")
    private String admissionPhone;

    @Field("entity_type")
    private CollegeEntityType entityType;

    @Field("faculty_count")
    private Integer facultyCount;

    @Field("student_count")
    private Integer studentCount;

    @Field("official_url_brochure")
    private String officialUrlBrochure;

    @Field("alternate_names")
    private List<String> alternateNames;

    @Field("institute_types")
    private List<String> instituteTypes;

    @Field("last_updated")
    private Date lastUpdated;

    @Field("common_name")
    private String commonName;

    @Field("official_name")
    @JsonProperty("official_name")
    private String officialName;

    @Field("ownership")
    private String ownership;

    @Field("phone")
    private String phone;

    @Field("publishing_status")
    private String publishingStatus;

    @Field("url")
    private String url;

    @Field("established_year")
    private Integer establishedYear;

    @Field("former_name")
    private List<String> formerName;

    @Field("official_address")
    @JsonProperty("official_address")
    private OfficialAddress officialAddress;

    @Field("facilities")
    private List<String> facilities;

    @Field("genders_accepted")
    private List<String> gendersAccepted;

    @Field("salaries_placement")
    private List<Placement> salariesPlacement;

    @Field("companies_placement")
    private List<Placement> companiesPlacement;

    @Field("rankings")
    private List<Ranking> rankings;

    @Field("gallery")
    @JsonProperty("gallery")
    private Gallery gallery;

    @Field("accreditations")
    private List<Accreditation> accreditations;

    @Field("approvals")
    @JsonProperty("approvals")
    private List<String> approvals;

    @Field("institution_state")
    private String institutionState;

    @Field("institution_city")
    private String institutionCity;

    @Field("notable_alumni")
    private List<Alumni> notableAlumni;

    @Field("campus_size")
    private Integer campusSize;

    @Field("total_enrollments")
    private Integer totalEnrollments;

    @Field("is_client")
    private int isClient;

    public Institute(String commonName, Long instituteId) {
        this.commonName = commonName;
        this.instituteId = instituteId;
    }

}
