package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
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
    @JsonProperty("cta_id")
    private Long ctaId;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("cta_type")
    @JsonProperty("cta_type")
    private String ctaType;

    @Field("logo_url")
    @JsonProperty("logo_url")
    private String logoUrl;

    @Field("cta_url")
    @JsonProperty("cta_url")
    private String url;

    @Field("properties")
    private List<String> properties;
}
