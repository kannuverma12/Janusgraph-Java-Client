package com.paytm.digital.education.explore.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum SchoolBoardType {
    CBSE("Central Board of Secondary Education"),
    IB("International Baccalaureate"),
    CISCE("Council for Indian School Certificate Examinations"),
    CAMBRIDGE_INT("Cambridge Assessment International Education"),
    GUJARAT_BOARD("Gujarat Secondary and Higher Secondary Education Board"),
    KARNATKA_BOARD("Karnataka Secondary Education Examination Borad"),
    MADHYA_PRADESH_BOARD("Madhya Pradesh Board of Secondary Education"),
    NATIONAL_INSTITUTE_OF_OPEN_SCHOOLING("National Institute of Open Schooling");

    private final String readableValue;

    SchoolBoardType(String readableValue) {
        this.readableValue = readableValue;
    }

    @JsonValue
    public String getReadableValue() {
        return this.readableValue;
    }
}
