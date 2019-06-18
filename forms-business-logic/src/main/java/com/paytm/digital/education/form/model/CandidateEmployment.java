package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CandidateEmployment {

    private String status;

    private String organization;

    private String isStateOwned;

    private String designation;

    private String nature;

    private String gazettedPost;

    private String type;

    private String startDate;

    private String endDate;
}
