package com.paytm.digital.education.explore.database.ingestion;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;


import java.io.Serializable;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormSubmissionDate  implements Serializable {

    private static final long serialVersionUID = -175361454362888L;

    @Field("date_range_start")
    @JsonProperty("date_range_start")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateRangeStart;

    @Field("date_range_end")
    @JsonProperty("date_range_end")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateRangeEnd;

    @Field("date")
    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;
}
