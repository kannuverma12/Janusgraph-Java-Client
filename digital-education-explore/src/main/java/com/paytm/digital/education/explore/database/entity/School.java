package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.SchoolEntityType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class School {

    @Id
    @Field("_id")
    @JsonIgnore
    private String id;

    @Field("school_id")
    @JsonProperty("school_id")
    private Long schoolId;

    @Field("entity_type")
    private SchoolEntityType schoolEntityType;

    @Field("name")
    @JsonProperty("name")
    private String officialName;

    @Field("official_address")
    @JsonProperty("official_address")
    private SchoolOfficialAddress officialAddress;

    @Field("short_name")
    @JsonProperty("short_name")
    private String shortName;

    @Field("former_name")
    @JsonProperty("former_name")
    private String formerName;

    @Field("school_size")
    @JsonProperty("school_size")
    private Double schoolSize;

    @Field("school_size_unit")
    @JsonProperty("school_size_unit")
    private String schoolSizeUnit;

    @Field("established_year")
    @JsonProperty("estb_year")
    private Integer establishedYear;

    @Field("last_updated")
    @JsonProperty("updated")
    private Date updated;

    @Field("boards")
    @JsonProperty("board")
    private List<Board> boardList;

    @Field("status")
    private String status;

    @Field("social_links")
    private List<SocialLink> socialLinks;

    @Field("teacher_to_student_ratio")
    private List<String> teacherToStudentRatio;

    @JsonProperty("phone")
    private String phone;

    @Field("gallery")
    @JsonProperty("gallery")
    private SchoolGallery gallery;

    @Field("school_area_name")
    private String schoolAreaName;

    @Field("activity_event_name")
    @JsonProperty("activity_event_name")
    private List<String> activityEventNames;

    @Field("pincode")
    @JsonProperty("pincode")
    private String pincode;

    public School(String officialName, Long schoolId) {
        this.officialName = officialName;
        this.schoolId = schoolId;
    }

}