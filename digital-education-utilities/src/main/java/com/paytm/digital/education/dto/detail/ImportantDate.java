package com.paytm.digital.education.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ImportantDate implements Serializable {

    private String      name;
    private String      examName;
    private List<Event> pastDates;
    private List<Event> upcomingDates;

}
