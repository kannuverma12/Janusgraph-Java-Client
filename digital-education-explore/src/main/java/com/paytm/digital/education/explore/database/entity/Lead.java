package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.CourseStream;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.enums.LeadAction;
import com.paytm.digital.education.explore.enums.LeadPartner;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    @JsonProperty(Constants.USER_ID)
    @Field(Constants.USER_ID)
    private Long userId;

    @JsonProperty(Constants.STREAM)
    @Field(Constants.STREAM)
    private CourseStream stream;

    @JsonProperty(Constants.INSTITUTE_ID)
    @Field(Constants.INSTITUTE_ID)
    private Long instituteId;

    @Field(Constants.CONTACT_NAME)
    @JsonProperty(Constants.CONTACT_NAME)
    @NotBlank(message = Constants.BLANK_NAME_VALIDATION_MESSAGE)
    @Pattern(regexp = Constants.NAME_REGEX, message = Constants.NAME_VALIDATION_MESSAGE)
    private String contactName;

    @Field(Constants.CONTACT_EMAIL)
    @JsonProperty(Constants.CONTACT_EMAIL)
    @Email(message = Constants.BLANK_EMAIL_VALIDATION_MESSAGE)
    @NotBlank(message = Constants.EMAIL_VALIDATION_MESSAGE)
    private String contactEmail;

    @Field(Constants.CONTACT_NUMBER)
    @JsonProperty(Constants.CONTACT_NUMBER)
    @NotBlank
    @Pattern(regexp = Constants.PHONE_REGEX, message = Constants.PHONE_VALIDATION_MESSAGE)
    private String contactNumber;

    @Field(Constants.STATE_ID)
    @JsonProperty(Constants.STATE_ID)
    @NotNull
    private Long stateId;

    @Field(Constants.CITY_ID)
    @JsonProperty(Constants.CITY_ID)
    @NotNull
    private Long cityId;

    @Field(Constants.ENTITY_ID)
    @JsonProperty(Constants.ENTITY_ID)
    @NotNull
    @Min(1)
    private Long entityId;

    @Field(Constants.ENTITY_TYPE)
    @JsonProperty(Constants.ENTITY_TYPE)
    @NotNull
    private EducationEntity entityType;

    /**
     * action the user took to mark itself as a lead
     */
    @NotNull
    private LeadAction action;

    /**
     * Number of times a user has performed the {@link LeadAction}
     */
    @JsonIgnore
    @JsonProperty(Constants.ACTION_COUNT)
    @Field(Constants.ACTION_COUNT)
    private int actionCount;

    @Field(Constants.REQUEST_TYPE)
    @JsonIgnore
    private Integer requestType;

    @JsonIgnore
    @Field(Constants.INTERESTED)
    private Boolean interested;

    @JsonIgnore
    @Field(Constants.CREATED_AT)
    private Date createdAt;

    @JsonIgnore
    @Field(Constants.UPDATED_AT)
    private Date updatedAt;

    @JsonIgnore
    @Field(Constants.LEAD_RESPONSES)
    private List<BaseLeadResponse> baseLeadResponse;


    public static class Constants {
        public static final String ACTION                         = "action";
        public static final String STATUS                         = "status";
        public static final String USER_ID                        = "user_id";
        public static final String CONTACT_NAME                   = "contact_name";
        public static final String CONTACT_EMAIL                  = "contact_email";
        public static final String CONTACT_NUMBER                 = "contact_number";
        public static final String INSTITUTE_ID                   = "institute_id";
        public static final String ACTION_COUNT                   = "action_count";
        public static final String STATE_ID                       = "state_id";
        public static final String CITY_ID                        = "city_id";
        public static final String ENTITY_ID                      = "entity_id";
        public static final String STREAM                         = "stream";
        public static final String REQUEST_TYPE                   = "request_type";
        public static final String ENTITY_TYPE                    = "entity_type";
        public static final String CREATED_AT                     = "created_at";
        public static final String UPDATED_AT                     = "updated_at";
        public static final String LEAD_RESPONSES                 = "lead_responses";
        public static final String INTERESTED                     = "interested";
        public static final String LAST_ACTION                    = "last_action";
        public static final String THIRD_PARTY_RESPONSES          = "third_party_response";
        public static final String PHONE_REGEX                    =
                "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";
        public static final String NAME_REGEX                     = "^[\\p{L} .'-]+$";
        public static final String PHONE_VALIDATION_MESSAGE       = "Invalid phone number.";
        public static final String NAME_VALIDATION_MESSAGE        =
                "Name must only contain Alphabets & Spaces.";
        public static final String BLANK_NAME_VALIDATION_MESSAGE  = "Name cannot be left Blank.";
        public static final String BLANK_EMAIL_VALIDATION_MESSAGE = "Enter a Valid Email Id.";
        public static final String EMAIL_VALIDATION_MESSAGE       = "Email cannot be left Blank.";
    }
}
