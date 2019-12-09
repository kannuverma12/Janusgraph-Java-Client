package com.paytm.digital.education.explore.response.dto.detail.school.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.SchoolBoardType;
import com.paytm.digital.education.explore.response.dto.detail.ClassLevelRow;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassLevelTable implements Serializable {
    @JsonProperty("board")
    private SchoolBoardType boardType;

    @JsonProperty("class_level_rows")
    private List<ClassLevelRow> classLevelRows;
}
