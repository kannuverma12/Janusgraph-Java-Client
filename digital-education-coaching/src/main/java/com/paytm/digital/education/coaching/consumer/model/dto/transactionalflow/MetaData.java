package com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MetaData {

    @Valid
    @NotNull
    private ConvTaxInfo convTaxInfo;

    @Valid
    @NotNull
    private TaxInfo taxInfo;

    @Valid
    @NotNull
    private TCS tcs;

    private String courseType;

    @NotNull
    private Long courseId;
    private Long userId;

    @NotEmpty
    private String merchantProductId;
}
