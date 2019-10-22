package com.paytm.digital.education.database.entity;

import java.util.List;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Data;

@Data
public class Unit {

    @Field("index")
    private int index;

    @Field("unit_name")
    private String name;

    @Field("topic")
    private List<Topic> topics;

}
