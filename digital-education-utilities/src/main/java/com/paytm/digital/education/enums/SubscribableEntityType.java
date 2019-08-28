package com.paytm.digital.education.enums;

import com.paytm.digital.education.daoresult.subscription.SubscriptionWithCourse;
import com.paytm.digital.education.daoresult.subscription.SubscriptionWithExam;
import com.paytm.digital.education.daoresult.subscription.SubscriptionWithInstitute;

public enum SubscribableEntityType {
    EXAM(SubscriptionWithExam.class),
    INSTITUTE(SubscriptionWithInstitute.class),
    COURSE(SubscriptionWithCourse.class);

    private final String correspondingCollectionName;
    private final Class correspondingClass;

    SubscribableEntityType(Class klass) {
        this.correspondingClass = klass;
        this.correspondingCollectionName = this.toString().toLowerCase();
    }

    public String getCorrespondingCollectionName() {
        return this.correspondingCollectionName;
    }

    public Class getCorrespondingClass() {
        return correspondingClass;
    }
}
