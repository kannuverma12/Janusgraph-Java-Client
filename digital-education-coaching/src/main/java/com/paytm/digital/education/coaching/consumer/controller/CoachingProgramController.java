package com.paytm.digital.education.coaching.consumer.controller;

import com.paytm.digital.education.coaching.consumer.model.response.GetCoachingProgramDetailsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.COACHING_PROGRAM_DETAILS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;

@Slf4j
@RestController
@RequestMapping(value = COACHING)
public class CoachingProgramController {

    @GetMapping(value = V1 + COACHING_PROGRAM_DETAILS)
    public GetCoachingProgramDetailsResponse getCoachingProgramDetails(
            @RequestParam(value = "programId") @NotNull final Long programId) {

        log.info("Got Coaching Program id : {}", programId);
        return GetCoachingProgramDetailsResponse.builder().build();
    }

}
