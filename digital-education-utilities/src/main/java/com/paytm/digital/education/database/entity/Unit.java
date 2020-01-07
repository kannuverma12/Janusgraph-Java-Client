package com.paytm.digital.education.database.entity;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Data;

@Data
public class Unit implements Serializable {

    private static final long serialVersionUID = -3620316449464027679L;

    @Field("index")
    private int index;

    @Field("unit_name")
    private String name;

    @Field("topic")
    private List<Topic> topics;

}
