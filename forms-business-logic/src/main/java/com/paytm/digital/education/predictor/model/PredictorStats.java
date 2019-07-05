package com.paytm.digital.education.predictor.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document("predictor_stats")
@Data
public class PredictorStats {

    @Id
    @Field("_id")
    private String id;

    @Field("merchantProductId")
    private String merchantProductId;

    @Field("merchantId")
    private String merchantId;

    @Field("customerId")
    private String customerId;

    @Field("entityId")
    private String entityId;

    @Field("useCount")
    private Integer useCount;

    @Field("updatedAt")
    private Date updatedAt;

    @Field("createdAt")
    private Date createdAt;

}
