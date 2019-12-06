package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.paytm.digital.education.enums.Gender;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cutoff implements Serializable {

    private static final long serialVersionUID = 2646583925783599734L;

    @Field("caste_group")
    private String casteGroup;

    @Field("counselling")
    private Counselling counselling;

    @Field("cutoff_type")
    private String cutoffType;

    @Field("exam_id")
    private long examId;

    @Field("final_value")
    private double finalValue;

    @Field("gender")
    private Gender gender;

    @Field("location")
    private String location;

    @Field("merit_list_type")
    private String meritListType;

    @Field("year")
    private int year;
}
