package com.paytm.digital.education.dto.detail;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Section implements Serializable {

    private static final long serialVersionUID = -512589898859943257L;

    @JsonProperty("name")
    private String name;

    @JsonProperty("units")
    List<Unit>     units;
}
