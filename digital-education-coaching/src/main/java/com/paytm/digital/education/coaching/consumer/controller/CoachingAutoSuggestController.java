package com.paytm.digital.education.coaching.consumer.controller;

import com.paytm.digital.education.coaching.consumer.model.response.suggest.AutoSuggestResponse;
import com.paytm.digital.education.coaching.consumer.service.search.CoachingAutoSuggestService;
import com.paytm.digital.education.enums.EducationEntity;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.GET_COCHING_AUTOSUGGESTIONS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;


@RestController
@RequestMapping(value = COACHING)
@AllArgsConstructor
@Validated
public class CoachingAutoSuggestController {

    private CoachingAutoSuggestService coachingAutoSuggestService;

    @GetMapping(value = V1 + GET_COCHING_AUTOSUGGESTIONS)
    public @ResponseBody AutoSuggestResponse autosuggestion(@RequestParam("query") String query,
            @RequestParam(value = "entities") List<EducationEntity> entities,
            @RequestParam(value = "size") @Min(1) int size) {

        return coachingAutoSuggestService.getSuggestions(query, entities, size);
    }

}
