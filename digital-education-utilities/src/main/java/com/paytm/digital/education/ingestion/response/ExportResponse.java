package com.paytm.digital.education.ingestion.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportResponse {

    private int countOfRecordsPresentInDb;
    private int countOfRecordsWritten;
}
