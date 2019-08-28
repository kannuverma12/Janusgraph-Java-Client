package com.paytm.digital.education.coaching.consumer.controller;

import com.paytm.digital.education.coaching.consumer.model.response.GetCoachingProgramDetailsResponse;
import com.paytm.digital.education.coaching.consumer.service.CoachingProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.COACHING_PROGRAM_DETAILS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;

@RestController
@RequestMapping(value = COACHING)
public class CoachingProgramController {

    @Autowired
    private CoachingProgramService coachingProgramService;

    @GetMapping(value = V1 + COACHING_PROGRAM_DETAILS)
    public GetCoachingProgramDetailsResponse getCoachingProgramDetails(
            @RequestParam(value = "id") @NotNull final Long programId,
            @RequestParam(value = "url_display_key") @NotEmpty final String urlDisplayKey) {
        return this.coachingProgramService.getProgramDetailsByIdAndUrlDisplayKey(programId, urlDisplayKey);
    }
}
