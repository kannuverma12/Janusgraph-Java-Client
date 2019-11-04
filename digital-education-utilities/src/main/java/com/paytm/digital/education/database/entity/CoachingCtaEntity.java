package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@Document("coaching_cta")
public class CoachingCtaEntity {

    @Id
    @Field("_id")
    @JsonIgnore
    private ObjectId id;

    @Field("cta_id")
    private Long ctaId;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("cta_type")
    private String ctaType;

    @Field("logo_url")
    private String logoUrl;

    @Field("cta_url")
    private String url;

    @Field("properties")
    private Map<String, String> properties;
}
