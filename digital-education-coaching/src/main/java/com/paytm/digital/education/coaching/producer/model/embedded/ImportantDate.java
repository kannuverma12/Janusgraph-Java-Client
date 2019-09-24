package com.paytm.digital.education.coaching.producer.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class ImportantDate {

    @NotEmpty
    @Size(max = 50)
    private String key;

    @NotEmpty
    @Size(max = 50)
    private String value;
}
