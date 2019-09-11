package com.paytm.digital.education.coaching.ingestion.model.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertiesResponse {

    private String sheetId;
    private String headerRange;
    private double startRow;
    private String dataRangeTemplate;
}
