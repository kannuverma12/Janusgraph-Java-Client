package com.paytm.digital.education.form.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FulfilmentKafkaPostDataObject {

    @JsonProperty("post_actions")
    String postActions;

    String status;
}
