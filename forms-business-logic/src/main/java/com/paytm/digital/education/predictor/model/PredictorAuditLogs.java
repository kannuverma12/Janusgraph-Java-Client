package com.paytm.digital.education.predictor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Document("predictor_audit_logs")
public class PredictorAuditLogs {

    @Id
    @Field("_id")
    private ObjectId mongoId;

    @Field("refId")
    private String refId;

    @Field("customerId")
    private String customerId;

    @Field("merchantId")
    private String merchantId;

    @Field("candidateId")
    private String candidateId;

    @Field("requestData")
    private List<Map<String, Object>> requestData;

    @Field("responseData")
    private List<Map<String, Object>> responseData;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;
}
