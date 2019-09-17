package com.paytm.digital.education.coaching.consumer.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TCS {

    @NotNull
    private Float basePrice;

    @NotNull
    private Float sgst;

    @NotNull
    private Float cgst;

    @NotNull
    private Float igst;

    @NotNull
    private Float utgst;

    private Long   sac;
    private Long   spin;
    private Long   dpin;
    private String agstin;
    private String hsn;
    private Long   cpin;
    private String cgstin;
}
