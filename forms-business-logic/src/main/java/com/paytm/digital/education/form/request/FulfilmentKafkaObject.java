package com.paytm.digital.education.form.request;

import lombok.Data;

@Data
public class FulfilmentKafkaObject {

    Long fulfilmentId;

    Long orderId;

    String url;

    FulfilmentKafkaPostDataObject postData;
}
