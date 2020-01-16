package com.paytm.digital.education.explore.request.dto.institute.by.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.Action;
import com.paytm.digital.education.enums.Product;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ActionRequest {
    @NotNull
    private Product product;

    private String email;

    @NotNull
    @JsonProperty("explore_institute_id")
    private Long exploreInstituteId;

    @NotNull
    private Action action;
}
