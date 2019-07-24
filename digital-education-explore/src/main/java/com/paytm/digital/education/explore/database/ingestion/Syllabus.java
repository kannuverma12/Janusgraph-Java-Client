package com.paytm.digital.education.explore.database.ingestion;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Syllabus {

    @JsonProperty("index")
    @Field("index")
    private Long index;

    @JsonProperty("subject_name")
    @Field("subject_name")
    private String subjectName;

    @JsonProperty("unit")
    @Field("unit")
    private List<Unit> units;

    @JsonProperty("subject_marks")
    @Field("subject_marks")
    private Double subjectMarks;
}
