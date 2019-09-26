package com.paytm.digital.education.coaching.consumer.controller;

import com.paytm.digital.education.coaching.consumer.model.response.details.GetExamDetailsResponse;
import com.paytm.digital.education.coaching.consumer.service.details.ExamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.COACHING_BASE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.GET_EXAM_DETAILS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;

@Slf4j
@RestController
@RequestMapping(value = COACHING_BASE + V1)
public class ExamController {

    @Autowired private ExamService examService;

    @GetMapping(value = GET_EXAM_DETAILS)
    public GetExamDetailsResponse getExamDetails(
            @RequestParam(value = "exam_id") @NotNull final Long examId,
            @RequestParam(value = "url_display_key") @NotEmpty final String urlDisplayKey) {

        return examService.getExamDetails(examId, urlDisplayKey);
    }
}
