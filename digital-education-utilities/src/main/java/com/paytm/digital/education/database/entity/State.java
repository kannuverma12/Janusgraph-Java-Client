package com.paytm.digital.education.database.entity;

import com.paytm.digital.education.enums.StateType;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class State {

    private String id;

    @Indexed(unique = true)
    private String name;

    @Indexed
    private StateType type;

    public State(String name, StateType type) {
        this.name = name;
        this.type = type;
    }
}
