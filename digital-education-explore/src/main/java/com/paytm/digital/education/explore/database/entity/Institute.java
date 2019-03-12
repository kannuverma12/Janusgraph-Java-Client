package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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

@Data
@ToString
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
    @JsonProperty("parent_institution")
    private Long parentInstitution;

    @Field("admission_phone")
    @JsonProperty("admission_phone")
    private String admissionPhone;

    @Field("entity_type")
    @JsonProperty("entity_type")
    private CollegeEntityType entityType;

    @Field("faculty_count")
    @JsonProperty("faculty_count")
    private Integer facultyCount;

    @Field("student_count")
    @JsonProperty("student_count")
    private Integer studentCount;

    @Field("official_url_brochure")
    @JsonProperty("official_url_brochure")
    private String officialUrlBrochure;

    @Field("alternate_names")
    @JsonProperty("alternate_names")
    private List<String> alternateNames;

    @Field("institute_types")
    @JsonProperty("institute_types")
    private List<String> instituteTypes;

    @Field("last_updated")
    @JsonProperty("last_updated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date lastUpdated;

    @Field("common_name")
    @JsonProperty("common_name")
    private String commonName;

    @Field("official_name")
    @JsonProperty("official_name")
    private String officialName;

    @Field("ownership")
    @JsonProperty("ownership")
    private String ownership;

    @Field("phone")
    @JsonProperty("phone")
    private String phone;

    @Field("publishing_status")
    @JsonProperty("publishing_status")
    private String publishingStatus;

    @Field("url")
    @JsonProperty("url")
    private String url;

    @Field("established_year")
    @JsonProperty("established_year")
    private Integer establishedYear;

    @Field("former_name")
    @JsonProperty("former_name")
    private List<String> formerName;

    @Field("official_address")
    @JsonProperty("official_address")
    private OfficialAddress officialAddress;

    @Field("facilities")
    @JsonProperty("facilities")
    private List<String> facilities;

    @Field("genders_accepted")
    @JsonProperty("genders_accepted")
    private List<String> gendersAccepted;

    @Field("salaries_placement")
    @JsonProperty("salaries_placement")
    private List<Placement> salariesPlacement;

    @Field("companies_placement")
    @JsonProperty("companies_placement")
    private List<Placement> companiesPlacement;

    @Field("rankings")
    @JsonProperty("rankings")
    private List<Ranking> rankings;

    @Field("gallery")
    private Gallery gallery;

    @Field("accreditations")
    public List<Accreditation> accreditations;

    @Field("approvals")
    public List<String> approvals;

    public Institute(String commonName, Long instituteId) {
        this.commonName = commonName;
        this.instituteId = instituteId;
    }

}
