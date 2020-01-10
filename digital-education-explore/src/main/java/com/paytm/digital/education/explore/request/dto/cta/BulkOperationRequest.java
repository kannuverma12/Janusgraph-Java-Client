package com.paytm.digital.education.explore.request.dto.cta;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.BulkOperationType;
import lombok.Data;

@Data
public class BulkOperationRequest {
    @JsonProperty("type")
    private BulkOperationType bulkOperationType;
}
