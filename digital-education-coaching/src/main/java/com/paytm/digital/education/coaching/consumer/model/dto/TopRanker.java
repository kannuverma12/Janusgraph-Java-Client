package com.paytm.digital.education.coaching.consumer.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TopRanker {

    private long   id;
    private long   coachingInstituteId;
    private long   coachingCentreId;
    private long   coachingProgramId;
    private long   examId;
    private String name;
    private String image;
    private String rank;
    private String examDate;
    private String testimonial;
    private String signatureImage;
}
