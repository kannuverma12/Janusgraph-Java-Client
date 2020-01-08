package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.enums.Action;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Document("customer_action")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CustomerAction {

    @Id
    @JsonIgnore
    private ObjectId id;

    @Field("institute_by_product_entry_id")
    @JsonIgnore
    private ObjectId instituteByProductEntryId;

    @Field("email")
    private String email;

    @Field("action")
    private Action action;

    @Field("is_deleted")
    private Boolean isDeleted;

    @Field("created_at")
    @JsonIgnore
    private Date createdAt;

    @Field("updated_at")
    @JsonIgnore
    private Date updatedAt;

    public CustomerAction(ObjectId instituteByProductEntryId, String email, Action action) {
        this.instituteByProductEntryId = instituteByProductEntryId;
        this.email = email;
        this.action = action;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
}
