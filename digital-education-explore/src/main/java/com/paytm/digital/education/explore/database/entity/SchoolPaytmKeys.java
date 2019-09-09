package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Min;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class SchoolPaytmKeys extends PaytmKeys {
    @Field("pid")
    @JsonProperty("pid")
    @Min(1)
    private Long pid;

    @Field("mid")
    @JsonProperty("mid")
    @Min(1)
    private Long mid;

    @Field("form_id")
    @JsonProperty("form_id")
    private String formId;
}
