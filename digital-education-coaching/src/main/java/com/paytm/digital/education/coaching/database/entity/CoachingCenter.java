package com.paytm.digital.education.coaching.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@ToString
@Data
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoachingCenter {

    @Id
    @JsonIgnore
    @Field("_id")
    ObjectId id;

    @Field("institute_id")
    @JsonProperty("institute_id")
    private Long instituteId;

    @Field("center_id")
    @JsonProperty("center_id")
    private Long centerId;

    @Field("official_name")
    @JsonProperty("official_name")
    private String officialName;

    @Field("street_address1")
    @JsonProperty("street_address1")
    private String streetAddress1;

    @Field("street_address2")
    @JsonProperty("street_address2")
    private String streetAddress2;

    @Field("city")
    @JsonProperty("city")
    private String city;

    @Field("state")
    @JsonProperty("state")
    private String state;

    @Field("pincode")
    @JsonProperty("pincode")
    private Integer pincode;

    @Field("courses")
    @JsonProperty("courses")
    private List<Long> courseIds;

    @JsonProperty("active")
    @Field("active")
    private boolean active;
}
