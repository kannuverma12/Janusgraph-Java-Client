package com.paytm.digital.education.dto.detail;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Syllabus {

    @JsonProperty("sub_exam_name")
    private String        subExamName;

    @JsonProperty("sections")
    private List<Section> sections;

}
