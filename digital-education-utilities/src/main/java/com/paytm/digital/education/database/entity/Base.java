package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Base implements Serializable {

    private static final long serialVersionUID = -4547824612782892387L;

    @Field("created_at")
    @CreatedDate
    @JsonIgnore
    private LocalDateTime createdAt;

    @Field("updated_at")
    @LastModifiedDate
    @JsonIgnore
    private LocalDateTime updatedAt;

    @Field("is_enabled")
    private Boolean isEnabled;

    @Field("priority")
    private Integer priority = new Integer(0);
}
