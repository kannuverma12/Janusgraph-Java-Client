package com.paytm.digital.education.explore.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.ESIngestionStatus;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.enums.RecentDocumentType;
import lombok.Data;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchHistory {

    private String id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("terms")
    private String terms;

    @JsonProperty("status")
    private ESIngestionStatus status;

    @JsonProperty("failure_message")
    private String failureMessage;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("updated_at")
    private Date updatedAt;

    @JsonProperty("es_ingestion_retries")
    private int esIngestionRetries;

    @JsonProperty("entity")
    private EducationEntity educationEntity;

    @JsonProperty("document_type")
    private RecentDocumentType docType;

}
