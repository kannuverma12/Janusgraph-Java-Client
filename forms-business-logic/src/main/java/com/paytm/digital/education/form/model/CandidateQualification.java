package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CandidateQualification {

    private String type;

    private String percentage;

    private String resultStatus;

    private String college;

    private String university;

    private String degree;

    private String subject;

    private String state;

    private String resultDate;

    private Integer attempts;

    private String duration;

    private String currentClass;

    private String mode;

    private String compulsorySubjects;

    private String optionalSubjects;
}
