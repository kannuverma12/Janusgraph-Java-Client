package com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConvTaxInfo {

    @NotNull
    @JsonProperty("totalCGST")
    private Float totalCGST;

    @NotNull
    @JsonProperty("totalSGST")
    private Float totalSGST;

    @NotNull
    @JsonProperty("totalIGST")
    private Float totalIGST;

    @NotNull
    @JsonProperty("totalUTGST")
    private Float totalUTGST;
}
