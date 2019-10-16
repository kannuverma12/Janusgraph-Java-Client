package com.paytm.digital.education.ingestion.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResponse {
    private long countOfNewRecordsProcessed;
    private long countOfFailedRecordsProcessed;
    private long countOfNewRecordsFailed;
}
