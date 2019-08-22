package com.paytm.digital.education.coaching.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.coaching.response.dto.ResponseDto;
import lombok.Data;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@ToString
@Deprecated
@Data
@Document("coaching_center")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoachingCenter extends ResponseDto {
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

    @Field("street_address3")
    @JsonProperty("street_address3")
    private String streetAddress3;

    @Field("city")
    @JsonProperty("city")
    private String city;

    @Field("state")
    @JsonProperty("state")
    private String state;

    @Field("pincode")
    @JsonProperty("pincode")
    private Integer pincode;

    @Field("course_type_available")
    @JsonProperty("course_type_available")
    private List<CourseType> courseTypeAvailable;

    @JsonProperty("active")
    @Field("active")
    private boolean active;

    @JsonProperty("latitude")
    @Field("latitude")
    private String latitude;

    @JsonProperty("longitude")
    @Field("longitude")
    private String longitude;

    @Field("created_at")
    @JsonProperty("created_at")
    private Date createdAt;

    @Field("updated_at")
    @JsonProperty("updated_at")
    private Date updatedAt;
}
