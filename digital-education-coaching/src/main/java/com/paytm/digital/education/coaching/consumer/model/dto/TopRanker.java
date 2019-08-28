package com.paytm.digital.education.coaching.consumer.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TopRanker {

    private long       id;
    private long       coachingInstituteId;
    private long       coachingCentreId;
    private List<Long> coachingCourseIds;
    private long       examId;
    private String     name;
    private String     image;
    private String     rank;
    private String     examDate;
    private String     testimonial;
    private String     signatureImage;
}
