package com.paytm.digital.education.dto.detail;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Section {

    @JsonProperty("name")
    private String name;

    @JsonProperty("units")
    List<Unit>     units;
}
