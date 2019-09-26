package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.explore.database.entity.Event;
import com.paytm.digital.education.explore.database.entity.Instance;
import lombok.Data;

import java.util.Date;

@Data
public class EventInstanceDateHolder {
    private final Event    event;
    private final Instance instance;
    private final Date     date;
}
