package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Accreditation implements Serializable {

    private static final long serialVersionUID = -9149650655996815482L;

    @Field("name")
    public String name;

    @Field("grade")
    public String grade;

}
