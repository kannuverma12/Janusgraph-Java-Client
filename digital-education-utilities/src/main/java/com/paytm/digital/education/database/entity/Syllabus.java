package com.paytm.digital.education.database.entity;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Data;

@Data
public class Syllabus implements Serializable {

    private static final long serialVersionUID = -7452866317579005396L;

    @Field("index")
    private Long index;

    @Field("subject_name")
    private String subjectName;

    @Field("unit")
    private List<Unit> units;

    @Field("subject_marks")
    private Double subjectMarks;

}
