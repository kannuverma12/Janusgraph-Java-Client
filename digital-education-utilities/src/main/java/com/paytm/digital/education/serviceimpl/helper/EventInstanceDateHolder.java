package com.paytm.digital.education.serviceimpl.helper;

import com.paytm.digital.education.database.entity.Event;
import com.paytm.digital.education.database.entity.Instance;
import lombok.Data;

import java.util.Date;

@Data
public class EventInstanceDateHolder {
    private final Event    event;
    private final Instance instance;
    private final Date     date;

    public Integer instanceId() {
        return this.instance.getInstanceId();
    }
}
