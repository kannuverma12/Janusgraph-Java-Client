package com.paytm.digital.education.explore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CounsellingDto {

    @Field("id")
    @JsonProperty("id")
    private int id;

    @Field("name")
    @JsonProperty("name")
    private String name;
}
