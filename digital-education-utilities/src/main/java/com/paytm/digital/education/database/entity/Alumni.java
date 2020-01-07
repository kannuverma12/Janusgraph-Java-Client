package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Alumni implements Serializable {

    private static final long serialVersionUID = -2363595443245584662L;

    @Field("alumni_name")
    @JsonProperty("alumni_name")
    private String alumniName;

    @Field("current_designation")
    @JsonProperty("current_designation")
    private String currentDesignation;

    @Field("company_name")
    @JsonProperty("company_name")
    private String companyName;

    @Field("alumni_photo")
    @JsonProperty("alumni_photo")
    private String alumniPhoto;

}
