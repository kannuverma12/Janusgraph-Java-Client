package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.ClassType;
import com.paytm.digital.education.explore.enums.ShiftType;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShiftDetails {

    @JsonProperty("class_from")
    private ClassType classFrom;

    @JsonProperty("class_to")
    private ClassType classTo;

    @JsonProperty("shift_type")
    private ShiftType shiftType;
}
