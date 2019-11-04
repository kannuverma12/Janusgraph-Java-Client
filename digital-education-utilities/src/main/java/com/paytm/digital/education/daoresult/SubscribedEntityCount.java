package com.paytm.digital.education.daoresult;

import com.paytm.digital.education.enums.SubscribableEntityType;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SubscribedEntityCount {
    private long count;
    private SubscribableEntityType entity;
}
