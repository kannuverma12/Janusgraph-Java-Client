package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.ESIngestionStatus;
import com.paytm.digital.education.explore.enums.EducationEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@Document("search_history")
public class SearchHistory {

    @Id
    @Field("_id")
    private String refId;

    @JsonProperty("user_id")
    @Field("user_id")
    private Long userId;

    @JsonProperty("terms")
    @Field("terms")
    private String terms;

    @JsonProperty("status")
    @Field("status")
    private ESIngestionStatus status;

    @JsonProperty("failure_message")
    @Field("failure_message")
    private String failureMessage;

    @JsonProperty("created_at")
    @Field("created_at")
    private Date createdAt;

    @JsonProperty("updated_at")
    @Field("updated_at")
    private Date updatedAt;

    @JsonProperty("entity")
    @Field("entity")
    private EducationEntity educationEntity;

}
