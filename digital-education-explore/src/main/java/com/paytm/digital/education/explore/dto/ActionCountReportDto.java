package com.paytm.digital.education.explore.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.opencsv.bean.CsvBindByName;
import com.paytm.digital.education.enums.Product;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ActionCountReportDto {

    @Field("explore_institute_id")
    @CsvBindByName(column = "explore_institute_id")
    private long exploreInstituteId;

    @Field("product")
    @CsvBindByName(column = "product")
    private Product product;

    @Field("count")
    @CsvBindByName(column = "count")
    private int count;
}
