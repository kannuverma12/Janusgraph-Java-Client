package com.paytm.digital.education.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SchoolBoardType {
    CBSE("Central Board of Secondary Education"),
    IB("International Baccalaureate"),
    CISCE("Council for Indian School Certificate Examinations"),
    CAMBRIDGE_INT("Cambridge Assessment International Education"),
    GUJARAT_BOARD("Gujarat Secondary and Higher Secondary Education Board"),
    KARNATKA_BOARD("Karnataka Secondary Education Examination Borad"),
    MADHYA_PRADESH_BOARD("Madhya Pradesh Board of Secondary Education"),
    NATIONAL_INSTITUTE_OF_OPEN_SCHOOLING("National Institute of Open Schooling"),
    KARNATKA_PRE_UNIVERSITY("Karnataka Board of the Pre-University Education"),
    MAHARASHTRA_BOARD("Maharashtra State Board of Secondary and Higher Secondary Education"),
    RAJASTHAN_BOARD("Rajasthan Board of Secondary Education"),
    TAMIL_BOARD("Tamil Nadu Board of Higher Secondary Education"),
    BANASTHALI_VIDYAPITH("Banasthali Vidyapith");

    private final String readableValue;

    SchoolBoardType(String readableValue) {
        this.readableValue = readableValue;
    }

    @JsonValue
    public String getReadableValue() {
        return this.readableValue;
    }
}
