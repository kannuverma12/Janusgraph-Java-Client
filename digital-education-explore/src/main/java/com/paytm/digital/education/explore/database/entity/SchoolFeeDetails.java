package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class SchoolFeeDetails {
    @Field("fees")
    @JsonProperty("fees")
    private Long feeAmount;

    @Field("fees_tenure")
    @JsonProperty("fees_tenure")
    private String feeTenure;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SchoolFeeDetails that = (SchoolFeeDetails) o;
        return Objects.equals(feeTenure, that.feeTenure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feeTenure);
    }
}
