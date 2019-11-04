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
@Document("coaching_banner")
public class CoachingBannerEntity extends Base {

    @Id
    @Field("_id")
    @JsonIgnore
    private ObjectId id;

    @Field("coaching_banner_id")
    @Indexed(unique = true)
    private Long coachingBannerId;

    @Field("banner_image_url")
    private String bannerImageUrl;

    @Field("redirection_url")
    private String redirectionUrl;
}
