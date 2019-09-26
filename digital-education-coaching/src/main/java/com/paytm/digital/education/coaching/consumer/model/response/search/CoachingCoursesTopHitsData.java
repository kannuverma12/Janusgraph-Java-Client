package com.paytm.digital.education.coaching.consumer.model.response.search;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class CoachingCoursesTopHitsData extends SearchBaseData {

    private Map<String, List<CoachingCourseData>> coursesPerStream;

}
