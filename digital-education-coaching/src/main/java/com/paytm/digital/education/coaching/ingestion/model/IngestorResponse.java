package com.paytm.digital.education.coaching.ingestion.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngestorResponse {

    private long countOfNewRecordsProcessed;
    private long countOfFailedRecordsProcessed;
}
