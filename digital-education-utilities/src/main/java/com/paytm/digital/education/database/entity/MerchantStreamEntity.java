package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Document("merchant_stream")
public class MerchantStreamEntity {

    @Id
    @Field("_id")
    @JsonIgnore
    private ObjectId id;

    @Field("stream")
    private String stream;

    @Field("paytm_stream_id")
    private Long paytmStreamId;

    @Field("merchant_id")
    private String merchantId;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;

    @Field("active")
    private Boolean active;

    public MerchantStreamEntity() {
        this.createdAt = new Date();
    }

}
