package com.paytm.digital.education.explore.database.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class State {

    private String id;

    @Indexed(unique = true)
    private String name;

    public State(String name) {
        this.name = name;
    }
}
