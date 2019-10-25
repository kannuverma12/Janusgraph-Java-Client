package com.paytm.digital.education.coaching.producer.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TopRankerDTO {

    private Long topRankerId;

    private Long instituteId;

    private Long centerId;

    private Long examId;

    private String studentName;

    private String studentPhoto;

    private List<Long> courseStudied;

    private String batchInfo;

    private String rankObtained;

    private String examYear;

    private String collegeAdmitted;

    private String testimonial;

}
