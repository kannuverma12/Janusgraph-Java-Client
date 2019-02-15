package com.paytm.digital.education.explore.daoresult;

import com.paytm.digital.education.explore.enums.SubscribableEntityType;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SubscribedEntityCount {
    private long count;
    private SubscribableEntityType entity;
}
