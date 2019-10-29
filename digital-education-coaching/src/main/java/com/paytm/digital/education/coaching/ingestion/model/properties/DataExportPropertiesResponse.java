package com.paytm.digital.education.coaching.ingestion.model.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataExportPropertiesResponse {

    private String sheetId;
}
