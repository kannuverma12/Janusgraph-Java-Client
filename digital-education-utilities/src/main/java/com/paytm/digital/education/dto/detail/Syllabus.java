package com.paytm.digital.education.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Syllabus implements Serializable {

    private static final long serialVersionUID = 4700187887740718222L;

    @JsonProperty("sub_exam_name")
    private String        subExamName;

    @JsonProperty("sections")
    private List<Section> sections;

}
