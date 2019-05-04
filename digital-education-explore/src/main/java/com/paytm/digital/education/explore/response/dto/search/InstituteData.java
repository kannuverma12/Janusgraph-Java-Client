package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.response.dto.common.OfficialAddress;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstituteData extends SearchBaseData {

    @JsonProperty("institute_id")
    private long instituteId;

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("exams")
    private List<String> exams;

    @JsonProperty("approvals")
    private Map<String, String> approvals;

    @JsonProperty("official_address")
    private OfficialAddress officialAddress;

}
