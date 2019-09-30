package com.paytm.digital.education.explore.es.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.database.entity.SchoolPaytmKeys;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchoolSearch {

    @JsonProperty("school_id")
    private long schoolId;

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("names")
    private List<String> names;

    @JsonProperty("area_name")
    private String areaName;

    @JsonProperty("state")
    private String state;

    @JsonProperty("city")
    private String city;

    @JsonProperty("year_of_estd")
    private int establishmentYear;

    @JsonProperty("campus_name")
    private String campusName;

    @JsonProperty("facilities")
    private List<String> facilities;

    @JsonProperty("image_link")
    private String imageLink;

    @JsonProperty("lang_medium")
    private List<String> languagesMedium;

    @JsonProperty("boards")
    private List<SchoolBoard> boards;

    @JsonProperty("paytm_keys")
    private SchoolPaytmKeys paytmKeys;

    @JsonProperty("location")
    private GeoLocation geoLocation;

    @JsonProperty("sort")
    private List<Double> sort;

    @JsonIgnore
    public String getBrochureUrl() {
        if (!CollectionUtils.isEmpty(boards)) {
            return boards.stream()
                    .filter(board -> StringUtils.isNotBlank(board.getSchoolBrochureUrl()))
                    .findFirst().map(SchoolBoard::getSchoolBrochureUrl).orElse(null);
        }
        return null;
    }

}
