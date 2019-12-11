package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.database.entity.Instance;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubExam implements Serializable {

    private static final long serialVersionUID = -8777541979474315663L;

    @Field("duration_hours")
    private Float durationHours;

    @Field("name")
    private String name;

    @Field("id")
    private Long id;

    @Field("instances")
    private List<Instance> instances;

    @Field("sub_exam_name")
    @JsonProperty("sub_exam_name")
    private String subExamName;

    @Field("published_status")
    private String publishedStatus;

}
