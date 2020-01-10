package com.paytm.digital.education.dto.detail;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static com.paytm.digital.education.constant.DBConstants.NON_TENTATIVE;
import static com.paytm.digital.education.utility.CommonUtils.setLastDateOfMonth;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event implements Serializable {

    private static final long serialVersionUID = -4075647661530875680L;

    @JsonProperty("name")
    private String name;

    @JsonProperty("date_start_range")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMMM,yyyy")
    private Date dateStartRange;

    @JsonProperty("date_end_range")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMMM,yyyy")
    private Date dateEndRange;

    @JsonProperty("date_start_range_timestamp")
    private Date dateStartRangeTimestamp;

    @JsonProperty("date_end_range_timestamp")
    private Date dateEndRangeTimestamp;

    @JsonProperty("type")
    private String type;

    @JsonProperty("type_display_name")
    private String typeDisplayName;

    @JsonProperty("mode")
    private List<String> modes;

    @JsonProperty("month")
    private String monthDate;

    @JsonProperty("month_timestamp")
    private Date monthTimestamp;

    @JsonProperty("certainity")
    private String certainity;

    @JsonProperty("date_name")
    private String dateName;

    @JsonProperty("ongoing")
    private Boolean ongoing;

    @JsonProperty("upcoming")
    private Boolean upcoming;

    @JsonProperty("other_event_label")
    private String otherEventLabel;

    public Date calculateCorrespondingDate() {
        Date eventDate;
        if (NON_TENTATIVE.equalsIgnoreCase(this.getCertainity())) {
            eventDate = (this.getDateEndRange() != null
                    ? this.getDateEndRange()
                    : this.getDateStartRange());
        } else {
            eventDate = setLastDateOfMonth(this.getMonthTimestamp());
        }
        return eventDate;
    }
}
