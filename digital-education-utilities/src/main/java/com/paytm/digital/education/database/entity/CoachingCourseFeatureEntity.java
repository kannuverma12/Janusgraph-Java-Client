package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@Document("coaching_course_feature")
public class CoachingCourseFeatureEntity extends Base {

    @Id
    @Field("_id")
    @JsonIgnore
    private ObjectId id;

    @Field("coaching_course_feature_id")
    @Indexed(unique = true)
    private Long coachingCourseFeatureId;

    @Field("institute_id")
    private Long instituteId;

    @Field("logo")
    private String logo;

    @Field("description")
    private String description;

    @Field("name")
    private String name;
}
