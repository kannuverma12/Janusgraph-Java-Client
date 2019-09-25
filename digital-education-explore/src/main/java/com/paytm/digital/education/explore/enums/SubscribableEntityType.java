package com.paytm.digital.education.explore.enums;

import com.paytm.digital.education.explore.daoresult.subscription.SubscriptionWithCourse;
import com.paytm.digital.education.explore.daoresult.subscription.SubscriptionWithExam;
import com.paytm.digital.education.explore.daoresult.subscription.SubscriptionWithInstitute;
import com.paytm.digital.education.explore.daoresult.subscription.SubscriptionWithSchool;

public enum SubscribableEntityType {
    EXAM(SubscriptionWithExam.class),
    INSTITUTE(SubscriptionWithInstitute.class),
    COURSE(SubscriptionWithCourse.class),
    SCHOOL(SubscriptionWithSchool.class);

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
