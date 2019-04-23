package com.paytm.digital.education.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationFlags {

    @JsonProperty("unread_shortlist")
    private Integer unreadShortlist;

    @JsonProperty("first_shortlist")
    private Integer firstShortlist;

    @JsonProperty("message")
    private String message;

    public NotificationFlags(String message) {
        this.message = message;
    }
}
