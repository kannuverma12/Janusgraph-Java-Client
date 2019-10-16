package com.paytm.digital.education.ingestion.sheets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.ingestion.annotation.GoogleSheetColumnName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantStreamForm {

    @JsonProperty("paytm_stream_id")
    @GoogleSheetColumnName("Paytm Stream Id")
    private Long paytmStreamId;

    @JsonProperty("merchant_id")
    @GoogleSheetColumnName("Merchant Id")
    private String merchantId;

    @JsonProperty("merchant_stream")
    @GoogleSheetColumnName("Merchant Stream")
    private String merchantStream;

    @JsonProperty("active")
    @GoogleSheetColumnName("active")
    private String active;
}
