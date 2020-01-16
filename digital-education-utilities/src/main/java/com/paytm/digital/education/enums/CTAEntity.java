package com.paytm.digital.education.enums;

import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.entity.School;
import lombok.Getter;

@Getter
public enum CTAEntity {
    SCHOOL(School.class),

    INSTITUTE(Institute.class),

    EXAM(Exam.class);

    private Class correspondingClass;

    CTAEntity(Class correspondingClass) {
        this.correspondingClass = correspondingClass;
    }
}
