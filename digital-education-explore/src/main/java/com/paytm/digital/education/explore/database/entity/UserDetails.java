package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document("user_details")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetails {

    @Id
    @Field(Constants.USER_ID)
    @JsonProperty(Constants.USER_ID)
    private Long userId;

    @Field(Constants.CONTACT_NAME)
    @JsonProperty(Constants.CONTACT_NAME)
    private String contactName;

    @Field(Constants.CONTACT_EMAIL)
    @JsonProperty(Constants.CONTACT_EMAIL)
    private String contactEmail;

    @Field(Constants.CONTACT_NUMBER)
    @JsonProperty(Constants.CONTACT_NUMBER)
    private String contactNumber;

    @Field(Constants.STATE_ID)
    @JsonProperty(Lead.Constants.STATE_ID)
    private Long stateId;

    @Field(Constants.CITY_ID)
    @JsonProperty(Constants.CITY_ID)
    private Long cityId;

    @Field(Constants.LOCATION)
    @JsonProperty(Constants.LOCATION)
    private String location;


    public static class Constants {
        public static final String CONTACT_NUMBER = "number";
        public static final String CONTACT_NAME   = "name";
        public static final String CONTACT_EMAIL  = "email";
        public static final String STATE_ID       = "state_id";
        public static final String CITY_ID        = "city_id";
        public static final String LOCATION       = "location";
        public static final String USER_ID        = "user_id";
    }

}
