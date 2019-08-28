package com.paytm.digital.education.coaching.consumer.controller;

import com.paytm.digital.education.coaching.consumer.model.response.GetCoachingCourseDetailsResponse;
import com.paytm.digital.education.coaching.consumer.service.CoachingCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.COACHING_COURSE_DETAILS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;

@RestController
@RequestMapping(value = COACHING)
public class CoachingCourseController {

    @Autowired
    private CoachingCourseService coachingCourseService;

    @GetMapping(value = V1 + COACHING_COURSE_DETAILS)
    public GetCoachingCourseDetailsResponse getCoachingCourseDetails(
            @RequestParam(value = "id") @NotNull final Long courseId,
            @RequestParam(value = "url_display_key") @NotEmpty final String urlDisplayKey) {
        return this.coachingCourseService.getCourseDetailsByIdAndUrlDisplayKey(courseId,
                urlDisplayKey);
    }
}
