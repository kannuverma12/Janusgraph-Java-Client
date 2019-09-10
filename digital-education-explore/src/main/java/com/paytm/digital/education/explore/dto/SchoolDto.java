package com.paytm.digital.education.explore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.database.entity.Board;
import com.paytm.digital.education.explore.database.entity.SchoolGallery;
import com.paytm.digital.education.explore.database.entity.SchoolOfficialAddress;
import com.paytm.digital.education.explore.database.entity.SocialLink;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("address")
    private SchoolOfficialAddress address;

    @JsonProperty("pincode")
    private String pincode;

    @JsonProperty("short_name")
    private String shortName;

    @JsonProperty("former_name")
    private String formerName;

    @JsonProperty("school_size")
    private Double schoolSize;

    @JsonProperty("school_size_unit")
    private String schoolSizeUnit;

    @JsonProperty("estb_year")
    private Integer establishedYear;

    @JsonProperty("updated")
    private Date updated;

    @JsonProperty("board")
    private List<Board> boardList;

    @JsonProperty("status")
    private String status;

    @JsonProperty("social_links")
    private List<SocialLink> socialLinks;

    @JsonProperty("teacher_to_student_ratio")
    private List<String> teacherToStudentRatio;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("official_address")
    private SchoolOfficialAddress officialAddress;

    @JsonProperty("gallery")
    private SchoolGallery gallery;

    @JsonProperty("school_area_name")
    private String schoolAreaName;

    @JsonProperty("campus_name")
    private String campusName;

    @JsonProperty("activity_event_name")
    private List<String> activityEventNames;

}
