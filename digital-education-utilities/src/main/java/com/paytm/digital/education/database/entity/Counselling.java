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
public class Counselling implements Serializable {

    private static final long serialVersionUID = 4538169349653055549L;

    @Field("id")
    @JsonProperty("id")
    private int id;

    @Field("name")
    @JsonProperty("name")
    private String name;
}
