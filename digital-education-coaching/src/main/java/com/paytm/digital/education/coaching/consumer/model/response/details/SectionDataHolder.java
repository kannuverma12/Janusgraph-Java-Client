package com.paytm.digital.education.coaching.consumer.model.response.details;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.enums.ExamSectionType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SectionDataHolder {

    private String          icon;
    private String          displayName;
    private String          snippetText;
    private ExamSectionType examSectionType;

}
