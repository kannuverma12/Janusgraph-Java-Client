package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.ClassType;
import com.paytm.digital.education.explore.enums.ClassLevel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClassLevelRow {
    @JsonProperty("education_level")
    private ClassLevel educationLevel;

    @JsonProperty("class_from")
    private ClassType classFrom;

    @JsonProperty("class_to")
    private ClassType classTo;
}
