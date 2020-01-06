package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.ClassType;
import com.paytm.digital.education.enums.SchoolAdmissionMode;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class SchoolAdmission implements Serializable {
    private static final long serialVersionUID = -8374521150347552708L;

    @Field("start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("start_date")
    private Date startDate;

    @Field("end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("end_date")
    private Date endDate;

    @Field("modes")
    private List<SchoolAdmissionMode> modes;

    @Field("date_type")
    @JsonProperty("date_type")
    private String dateType;

    @Field("class_from")
    @JsonProperty("class_from")
    private ClassType classFrom;

    @Field("class_to")
    @JsonProperty("class_to")
    private ClassType classTo;

    public SchoolAdmission(SchoolAdmission schoolAdmission) {
        this.startDate = schoolAdmission.getStartDate();
        this.endDate = schoolAdmission.getEndDate();
        this.modes = schoolAdmission.getModes();
        this.dateType = schoolAdmission.getDateType();
        this.classFrom = schoolAdmission.getClassFrom();
        this.classTo = schoolAdmission.getClassTo();
    }
}
