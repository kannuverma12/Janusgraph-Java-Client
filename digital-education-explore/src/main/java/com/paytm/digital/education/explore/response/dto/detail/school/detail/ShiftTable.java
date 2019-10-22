package com.paytm.digital.education.explore.response.dto.detail.school.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.SchoolBoardType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ShiftTable {
    @JsonProperty("board")
    private SchoolBoardType boardType;

    @JsonProperty("shift_rows")
    private List<ShiftDetailsResponse> shiftDetailsRows;
}
