package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Objects;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolFeeDetails implements Serializable {

    private static final long serialVersionUID = -979917408344474322L;

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
