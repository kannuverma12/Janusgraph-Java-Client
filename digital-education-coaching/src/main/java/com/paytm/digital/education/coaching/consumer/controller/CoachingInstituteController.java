package com.paytm.digital.education.coaching.consumer.controller;

import com.paytm.digital.education.coaching.consumer.model.response.GetCoachingInstituteDetailsResponse;
import com.paytm.digital.education.coaching.consumer.service.details.CoachingInstituteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.COACHING_BASE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.GET_COACHING_INSTITUTE_DETAILS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;

@Slf4j
@RestController
@RequestMapping(value = COACHING_BASE + V1)
public class CoachingInstituteController {

    @Autowired
    private CoachingInstituteService coachingInstituteService;

    @GetMapping(value = GET_COACHING_INSTITUTE_DETAILS)
    public GetCoachingInstituteDetailsResponse getCoachingInstituteDetails(
            @RequestParam(value = "institute_id") @NotNull final Long instituteId,
            @RequestParam(value = "url_display_key") @NotEmpty final String urlDisplayKey,
            @RequestParam(value = "stream_id", required = false) final Long streamId,
            @RequestParam(value = "exam_id", required = false) final Long examId) {
        return coachingInstituteService
                .getCoachingInstituteDetails(instituteId, urlDisplayKey, streamId, examId);
    }
}
