package com.paytm.digital.education.explore.database.entity;

import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "subscription")
public class Subscription {

    @Id
    private ObjectId id;

    @Field("user_id")
    private long userId;

    @Field("entity")
    private EducationEntity entity;

    @Field("entity_id")
    private long entityId;

    @Field("status")
    private SubscriptionStatus status;

    @CreatedDate
    @Field("created_at")
    private Date createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Date lastModified;

    public Subscription(long userId, EducationEntity subscriptionEntity, long clgId,
            SubscriptionStatus subscriptionStatus, Date createdAt, Date updatedAt) {
        this.userId = userId;
        this.entity = subscriptionEntity;
        this.entityId = clgId;
        this.status = subscriptionStatus;
        this.createdAt = createdAt;
        this.lastModified = updatedAt;
    }

}
