package com.paytm.digital.education.coaching.consumer.model.response;

import com.paytm.digital.education.coaching.consumer.model.dto.Exam;
import com.paytm.digital.education.coaching.consumer.model.dto.InstituteHighLight;
import com.paytm.digital.education.coaching.consumer.model.dto.Stream;
import com.paytm.digital.education.coaching.consumer.model.dto.TopRanker;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetCoachingInstituteDetailsResponse {

    private long                     instituteId;
    private String                   instituteName;
    private String                   imageUrl;
    private String                   description;
    private List<InstituteHighLight> instituteHighLights;
    private List<Stream>             streams;
    private List<Exam>               exams;
    private List<TopRanker>          topRankers;
    private List<String>             coachingCourseTypes;
}
