package com.paytm.digital.education.explore.response.dto.detail;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ClassInfoLegend {
    private String educationLevel;
    private String classFrom;

    public static final List<ClassInfoLegend> CLASS_INFO_STATIC_CONTENT_LIST = new ArrayList<>();

    static {
        CLASS_INFO_STATIC_CONTENT_LIST.add(
                new ClassInfoLegend("Primary School", "1st - 5th"));
        CLASS_INFO_STATIC_CONTENT_LIST.add(
                new ClassInfoLegend("Upper Primary/Middle School", "1st - 5th"));
        CLASS_INFO_STATIC_CONTENT_LIST.add(
                new ClassInfoLegend("Sr. Secondary", "1st - 5th"));
    }
}
