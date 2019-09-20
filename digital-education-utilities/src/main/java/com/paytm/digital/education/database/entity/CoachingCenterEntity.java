package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paytm.digital.education.database.embedded.OfficialAddress;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@Document("coaching_center")
public class CoachingCenterEntity extends Base {

    @Id
    @Field("_id")
    @JsonIgnore
    ObjectId id;

    @Field("institute_id")
    private Long instituteId;

    @Indexed(unique = true)
    @Field("center_id")
    private Long centerId;

    @Field("official_name")
    private String officialName;

    @Field("official_address")
    private OfficialAddress officialAddress;

    @Field("course_types")
    private List<String> courseTypes;

    @Field("opening_time")
    private LocalTime openingTime;

    @Field("closing_time")
    private LocalTime closingTime;

    @Field("center_image")
    private String centerImage;

}
