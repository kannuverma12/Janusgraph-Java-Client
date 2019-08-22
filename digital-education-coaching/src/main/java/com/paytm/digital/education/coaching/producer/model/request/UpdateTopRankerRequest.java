package com.paytm.digital.education.coaching.producer.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTopRankerRequest {

    @NotNull private  Long   topRankerId;
    @NotNull private  Long   instituteId;
    private           Long   centerId;
    @NotNull private  Long   examId;
    @NotEmpty private String studentName;
    @NotEmpty private String studentPhoto;
    @NotEmpty private String courseStudied;
    @NotEmpty private String year;
    @NotEmpty private String batch;
    @NotEmpty private String rankObtained;
    @NotEmpty private String examYear;
    private           String collegeAdmitted;
    private           String testimonial;
}
