package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.SubscribableEntityType;
import com.paytm.digital.education.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document("subscription")
public class Subscription {

    private String id;

    @Field("user_id")
    @JsonProperty("user_id")
    private Long userId;

    @Field("entity")
    @JsonProperty("entity")
    private SubscribableEntityType subscribableEntityType;

    @Field("entity_id")
    private Long entityId;

    @Field("status")
    private SubscriptionStatus status;

    @CreatedDate
    @Field("created_at")
    private Date createdAt;

    @LastModifiedDate
    @Field("updated_at")
    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date lastModified;
    
    @Field("logo")
    @JsonProperty("logo_url")
    private String logoUrl;

    public Subscription(Long userId, SubscribableEntityType subscribableEntityType, Long entityId,
            SubscriptionStatus subscriptionStatus, Date createdAt, Date updatedAt) {
        this.userId = userId;
        this.subscribableEntityType = subscribableEntityType;
        this.entityId = entityId;
        this.status = subscriptionStatus;
        this.createdAt = createdAt;
        this.lastModified = updatedAt;
    }

    public Subscription(Long userId, SubscribableEntityType subscribableEntityType, Long entityId,
                        SubscriptionStatus subscriptionStatus) {
        this.userId = userId;
        this.subscribableEntityType = subscribableEntityType;
        this.entityId = entityId;
        this.status = subscriptionStatus;
        this.createdAt = new Date();
        this.lastModified = new Date();
    }

    public interface Constants {
        String USER_ID = "user_id";
        String ENTITY = "entity";
        String ENTITY_ID = "entity_id";
        String STATUS = "status";
        String LAST_UPDATED = "updated_at";
    }

}
