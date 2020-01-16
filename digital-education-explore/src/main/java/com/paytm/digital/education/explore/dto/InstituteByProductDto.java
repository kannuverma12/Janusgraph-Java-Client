package com.paytm.digital.education.explore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.database.entity.InstituteByProduct;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class InstituteByProductDto extends InstituteByProduct {
    private static final long serialVersionUID = 2116742663073310903L;

    @Field("has_shown_interest")
    private boolean hasShownInterest;
}
