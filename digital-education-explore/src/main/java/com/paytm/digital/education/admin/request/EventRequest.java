package com.paytm.digital.education.admin.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Validated
public class EventRequest {
    @JsonProperty("event_type")
    private String eventType;

    @NotBlank
    @JsonProperty("event_title")
    private String eventTitle;

    @NotBlank
    @JsonProperty("event_description")
    private String eventDescription;

    @NotNull
    @Size(min = 10, max = 10, message = "Enter 10 digits mobile number")
    @Min(value = 1000000000, message = "Enter 10 digits mobile number")
    @JsonProperty("submitted_by_:_institute_paytm_phone_number")
    private String submittedBy;

    @NotNull
    @Min(1)
    @JsonProperty("institute_id")
    private Long instituteId;

    @NotBlank
    @JsonProperty("timestamp")
    private String timestamp;

    @NotBlank
    @JsonProperty("event_media")
    private String eventMedia;

    @JsonProperty("email_address")
    private String emailAddress;
}
