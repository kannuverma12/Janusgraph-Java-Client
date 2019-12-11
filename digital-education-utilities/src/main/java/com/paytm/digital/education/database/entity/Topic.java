package com.paytm.digital.education.database.entity;

import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Data;

import java.io.Serializable;

@Data
public class Topic implements Serializable {

    private static final long serialVersionUID = -2075853282387292259L;

    @Field("topic_name")
    private String name;

}
