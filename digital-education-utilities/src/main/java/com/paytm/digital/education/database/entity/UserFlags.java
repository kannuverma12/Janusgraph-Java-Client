package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document("user_flags")
public class UserFlags {

    @Field("user_id")
    private long userId;

    @Field("unread_shortlist_count")
    private Integer unreadShortlistCount;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;

    public UserFlags() {
        this.createdAt = new Date();
        this.updatedAt = this.createdAt;
    }

    @JsonIgnore
    public int getShortlistFlag() {
        if (this.getUnreadShortlistCount() != null && unreadShortlistCount > 0) {
            return 1;
        }
        return 0;
    }
}
