package com.paytm.digital.education.explore.response.dto.search;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ExamSectionData extends SearchBaseData {

    private Map<String, List<ExamSubItemData>> examsPerLevel;

}
