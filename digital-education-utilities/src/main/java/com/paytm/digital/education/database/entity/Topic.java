package com.paytm.digital.education.database.entity;

import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Data;

@Data
public class Topic {

    @Field("topic_name")
    private String name;

}
