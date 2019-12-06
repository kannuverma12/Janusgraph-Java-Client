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
public class CourseFee implements Serializable {

    private static final long serialVersionUID = -5513807506159317843L;

    @Field("fee")
    private Integer fee;

    @Field("caste_group")
    private String casteGroup;
}
