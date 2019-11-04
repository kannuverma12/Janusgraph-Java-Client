package com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.consumer.model.dto.SyllabusAndBrochure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingCourseDetails {

    private String              header;
    private Map<String, String> courseDetailsInfo;
    private Map<String, String> courseDetailsMoreInfo;
    private SyllabusAndBrochure syllabusAndBrochure;
}
