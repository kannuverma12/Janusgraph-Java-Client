package com.paytm.digital.education.explore.database.entity;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Data;

@Data
public class Syllabus {

    @Field("index")
    private Long index;

    @Field("subject_name")
    private String subjectName;

    @Field("unit")
    private List<Unit> units;

    @Field("subject_marks")
    private Double subjectMarks;

}
