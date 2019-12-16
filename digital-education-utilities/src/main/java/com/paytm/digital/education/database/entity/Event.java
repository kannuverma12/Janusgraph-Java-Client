package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static com.paytm.digital.education.constant.DBConstants.NON_TENTATIVE;
import static com.paytm.digital.education.constant.DBConstants.YYYY_MM;
import static com.paytm.digital.education.utility.DateUtil.stringToDate;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event implements Serializable {

    private static final long serialVersionUID = 6782077015608253201L;

    @Field("month")
    private String monthDate;

    @Field("event_id")
    private long eventId;

    @Field("modes")
    private List<String> modes;

    @Field("type")
    private String type;

    @Field("other_event_label")
    private String otherEventLabel;

    @Field("certainty")
    @JsonProperty("certainty")
    private String certainty;

    @Field("date")
    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @Field("date_range_start")
    @JsonProperty("date_range_start")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateRangeStart;

    @Field("date_range_end")
    @JsonProperty("date_range_end")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateRangeEnd;

    public Date calculateCorrespondingDate() {
        Date eventDate;
        if (NON_TENTATIVE.equalsIgnoreCase(this.getCertainty())) {
            eventDate = this.getDate() != null
                    ? this.getDate()
                    : this.getDateRangeStart();
        } else {
            eventDate = stringToDate(this.getMonthDate(), YYYY_MM);
        }
        return eventDate;
    }
}
