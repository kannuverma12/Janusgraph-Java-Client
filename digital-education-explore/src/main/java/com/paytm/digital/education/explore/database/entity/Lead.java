package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.enums.LeadAction;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Document
public class Lead {

    private String id;

    /**
     * denotes if current lead entry is active status (true) or not (false)
     */
    @JsonIgnore
    private boolean status = true;

    /**
     * Id of the current logged In user
     */
    @JsonIgnore
    @Field(Constants.USER_ID)
    private Long userId;

    @Field(Constants.CONTACT_NAME)
    @NotBlank
    private String contactName;

    @Field(Constants.CONTACT_EMAIL)
    @Email
    private String contactEmail;

    @Field(Constants.CONTACT_NUMBER)
    @NotBlank
    private String contactNumber;

    /**
     * action the user took to mark itself as a lead
     */
    @NotNull
    private LeadAction action;

    /**
     * Number of times a user has performed the {@link LeadAction}
     */
    @JsonIgnore
    @Field(Constants.ACTION_COUNT)
    private int actionCount;

    @Field(Constants.ENTITY_ID)
    @NotNull
    private String entityId;

    @Field(Constants.ENTITY_TYPE)
    @NotNull
    private EducationEntity entityType;

    @JsonIgnore
    @Field(Constants.CREATED_AT)
    private Date createdAt;

    @JsonIgnore
    @Field(Constants.UPDATED_AT)
    private Date updatedAt;

    public static class Constants {
        public static final String ACTION = "action";
        public static final String STATUS = "status";
        public static final String USER_ID = "user_id";
        public static final String CONTACT_NAME = "contact_name";
        public static final String CONTACT_EMAIL = "contact_email";
        public static final String CONTACT_NUMBER = "contact_number";
        public static final String ACTION_COUNT = "action_count";
        public static final String ENTITY_ID = "entity_id";
        public static final String ENTITY_TYPE = "entity_type";
        public static final String CREATED_AT = "created_at";
        public static final String UPDATED_AT = "updated_at";
    }
}
