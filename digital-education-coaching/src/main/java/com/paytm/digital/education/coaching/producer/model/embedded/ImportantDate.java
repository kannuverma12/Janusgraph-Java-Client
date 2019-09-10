package com.paytm.digital.education.coaching.producer.model.embedded;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class ImportantDate {

    @Size(max = 50)
    private String key;

    @Size(max = 50)
    private String value;
}
