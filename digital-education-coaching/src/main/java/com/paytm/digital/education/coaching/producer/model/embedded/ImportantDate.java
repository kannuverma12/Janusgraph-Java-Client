package com.paytm.digital.education.coaching.producer.model.embedded;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Size;

@Data
@Validated
public class ImportantDate {

    @Size(max = 50)
    private String key;

    @Size(max = 50)
    private String value;
}
