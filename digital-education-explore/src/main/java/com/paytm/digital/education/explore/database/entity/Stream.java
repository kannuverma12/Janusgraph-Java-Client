package com.paytm.digital.education.explore.database.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Stream {

    private String id;

    @Indexed(unique = true)
    private String name;

    public Stream(String name) {
        this.name = name;
    }
}
