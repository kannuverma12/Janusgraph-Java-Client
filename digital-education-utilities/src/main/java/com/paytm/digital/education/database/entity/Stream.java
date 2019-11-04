package com.paytm.digital.education.database.entity;

import static com.paytm.digital.education.utility.CustomStringUtils.convertStreamNameToDisplayName;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
public class Stream {

    private String id;

    @Indexed(unique = true)
    private String name;

    private String displayName;

    public Stream(String name) {
        this.name = name;
        this.displayName = convertStreamNameToDisplayName(name);
    }

    public Stream(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }
}
