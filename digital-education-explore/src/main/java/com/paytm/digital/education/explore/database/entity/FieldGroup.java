package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@ToString
@Document(collection = "field_groups")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldGroup {

    @JsonIgnore
    @Id
    private ObjectId id;

    @Field("name")
    private String name;

    @Field("entity")
    private String entity;

    @Field("fields")
    private List<String> fields;

    @Field("active")
    private boolean active;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;
}
