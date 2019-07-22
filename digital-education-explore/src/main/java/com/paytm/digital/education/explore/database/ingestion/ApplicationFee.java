package com.paytm.digital.education.explore.database.ingestion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationFee {
    @JsonProperty("category")
    @Field("category")
    private String category;

    @JsonProperty("quota")
    @Field("quota")
    private String quota;

    @JsonProperty("mode")
    @Field("mode")
    private String mode;

    @JsonProperty("gender")
    @Field("gender")
    private String gender;

    @JsonProperty("amount")
    @Field("amount")
    private Double amount;
}
