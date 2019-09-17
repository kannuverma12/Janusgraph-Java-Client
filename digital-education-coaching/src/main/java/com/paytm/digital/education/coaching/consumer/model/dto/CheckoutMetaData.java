package com.paytm.digital.education.coaching.consumer.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.enums.CourseType;
import lombok.Data;

import javax.validation.Valid;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CheckoutMetaData {

    @Valid
    private ConvTaxInfo convTaxInfo;

    @Valid
    private TaxInfo taxInfo;

    private CourseType courseType;
    private Long       courseId;
    private Long       userId;
}
