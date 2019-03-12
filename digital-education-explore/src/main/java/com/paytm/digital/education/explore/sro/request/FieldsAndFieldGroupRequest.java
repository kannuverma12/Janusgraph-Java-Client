package com.paytm.digital.education.explore.sro.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.annotation.FieldsAndFieldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@FieldsAndFieldGroup
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldsAndFieldGroupRequest {
    private List<String> fields;

    @JsonProperty("field_group")
    private String fieldGroup;
}
