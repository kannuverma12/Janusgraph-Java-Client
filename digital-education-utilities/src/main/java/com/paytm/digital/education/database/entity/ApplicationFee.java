package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationFee {
    @Field("category")
    private String category;

    @Field("quota")
    private String quota;

    @Field("mode")
    private String mode;

    @Field("gender")
    private String gender;

    @Field("amount")
    private Double amount;
}
