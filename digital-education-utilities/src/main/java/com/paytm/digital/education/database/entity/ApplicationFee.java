package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationFee implements Serializable {

    private static final long serialVersionUID = 8433163906540653736L;

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
