package com.paytm.digital.education.database.embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeyHighlight implements Serializable {

    @URL
    @NotEmpty
    private String logo;

    @NotEmpty
    @Size(max = 100)
    private String key;

    @NotEmpty
    @Size(max = 100)
    private String value;
}
