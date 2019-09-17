package com.paytm.digital.education.coaching.producer.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Size;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class ImportantDate {

    @Size(max = 50)
    private String key;

    @Size(max = 50)
    private String value;
}
