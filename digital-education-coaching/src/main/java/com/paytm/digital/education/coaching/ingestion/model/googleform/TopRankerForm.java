package com.paytm.digital.education.coaching.ingestion.model.googleform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TopRankerForm {

    private Long   topRankerId;
    private Long   instituteId;
    private Long   centerId;
    private Long   examId;
    private String studentName;
    private String studentPhoto;
    private String courseIds;
    private String yearAndBatch;
    private String rankObtained;
    private String examYear;
    private String collegeAdmitted;
    private String testimonial;
    private String category;

    private Integer globalPriority;
    private String  statusActive;
}
