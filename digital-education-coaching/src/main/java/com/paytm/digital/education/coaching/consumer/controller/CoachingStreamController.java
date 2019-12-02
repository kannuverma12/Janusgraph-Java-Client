package com.paytm.digital.education.coaching.consumer.controller;


import com.paytm.digital.education.coaching.consumer.model.response.details.GetStreamDetailsResponse;
import com.paytm.digital.education.coaching.consumer.service.details.CoachingStreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.COACHING_BASE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.GET_STREAM_DETAILS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;

@Slf4j
@RestController
@RequestMapping(value = COACHING_BASE + V1)
@Validated
public class CoachingStreamController {

    @Autowired
    private CoachingStreamService coachingStreamService;

    @GetMapping(value = GET_STREAM_DETAILS)
    public GetStreamDetailsResponse getStreamDetails(
            @RequestParam(value = "stream_id") @NotNull final long streamId,
            @RequestParam(value = "url_display_key") @NotEmpty final String urlDisplayKey) {

        return coachingStreamService.getStreamDetails(streamId, urlDisplayKey);
    }
}
