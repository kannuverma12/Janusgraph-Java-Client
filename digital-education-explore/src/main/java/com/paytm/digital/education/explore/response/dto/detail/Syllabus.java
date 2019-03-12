package com.paytm.digital.education.explore.response.dto.detail;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Syllabus {

    @JsonProperty("sub_exam_name")
    private String        subExamName;

    @JsonProperty("sections")
    private List<Section> sections;

}
