package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.ClassType;
import com.paytm.digital.education.enums.ShiftType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ShiftDetails {

    @Field("class_from")
    @JsonProperty("class_from")
    private ClassType classFrom;

    @Field("class_to")
    @JsonProperty("class_to")
    private ClassType classTo;

    @Field("shift_type")
    @JsonProperty("shift_type")
    private ShiftType shiftType;

    public ShiftDetails(ShiftDetails shiftDetails) {
        this.classFrom = shiftDetails.getClassFrom();
        this.classTo = shiftDetails.getClassTo();
        this.shiftType = shiftDetails.getShiftType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShiftDetails that = (ShiftDetails) o;
        return shiftType == that.shiftType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shiftType);
    }
}
