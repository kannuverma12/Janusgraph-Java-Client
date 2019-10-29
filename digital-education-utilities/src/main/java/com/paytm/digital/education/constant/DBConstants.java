package com.paytm.digital.education.constant;

import java.text.SimpleDateFormat;

public interface DBConstants {
    String COMPONENT = "component";
    String KEY       = "key";
    String NAMESPACE = "namespace";
    String USER_ID   = "user_id";
    String SEQUENCE  = "sequence";
    String NON_TENTATIVE = "non_tentative";
    String UNREAD_SHORTLIST_COUNT = "unread_shortlist_count";
    String SUCCESS                = "success";
    String GROUP_NAME   = "name";
    String GROUP_ENTITY = "entity";
    String GROUP_ACTIVE = "active";
    String EQ_OPERATOR = "$eq";
    String IN_OPERATOR = "$in";
    SimpleDateFormat YYYY_MM = new SimpleDateFormat("yyyy-MM");
}
