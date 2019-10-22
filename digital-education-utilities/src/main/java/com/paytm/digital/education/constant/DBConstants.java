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

    SimpleDateFormat YYYY_MM = new SimpleDateFormat("yyyy-MM");
}
