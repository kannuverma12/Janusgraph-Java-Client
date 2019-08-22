package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.database.embedded.OfficialAddress;
import com.paytm.digital.education.enums.CourseType;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Builder
@Document("coaching_center")
public class CoachingCenterEntity extends Base {

    @Id
    @Field("_id")
    @JsonIgnore
    ObjectId id;

    @Field("institute_id")
    @JsonProperty("institute_id")
    private Long instituteId;

    @Indexed(unique = true)
    @Field("center_id")
    @JsonProperty("center_id")
    private Long centerId;

    @Field("official_name")
    @JsonProperty("official_name")
    private String officialName;

    @Field("official_address")
    @JsonProperty(value = "official_address")
    private OfficialAddress officialAddress;

    @Field("course_types")
    @JsonProperty("course_types")
    private List<CourseType> courseTypes;

}
