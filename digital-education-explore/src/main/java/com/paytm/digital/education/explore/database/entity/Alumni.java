package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Alumni {

    @Field("alumni_name")
    @JsonProperty("alumni_name")
    private String alumniName;

    @Field("current_designation")
    @JsonProperty("current_designation")
    private String currentDesignation;

    @Field("alumni_photo")
    @JsonProperty("alumni_photo")
    private String alumniPhoto;

}
