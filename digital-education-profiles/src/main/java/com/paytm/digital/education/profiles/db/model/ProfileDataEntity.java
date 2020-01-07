package com.paytm.digital.education.profiles.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.database.entity.Base;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document("profile_data")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@CompoundIndex(def = "{'profile_id':1, 'key':1}", unique = true, name = "unique_profile_data")
public class ProfileDataEntity extends Base {

    @Id
    @Field("_id")
    @JsonIgnore
    private ObjectId id;

    @Field("customer_id")
    private Long customerId;

    @Field("profile_id")
    private Long profileId;

    @Field("key")
    private String key;

    @Field("value")
    private Object value;

    @Transient
    public Integer getPriority() {
        return super.getPriority();
    }

}
