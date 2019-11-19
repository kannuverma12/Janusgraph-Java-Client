package com.paytm.digital.education.coaching.consumer.controller;

import com.paytm.digital.education.coaching.consumer.model.request.SearchRequest;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResponse;
import com.paytm.digital.education.coaching.consumer.service.search.SearchService;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;

@RestController
@AllArgsConstructor
@RequestMapping(COACHING)
@Validated
public class CoachingSearchController {

    private static final Logger log = LoggerFactory.getLogger(CoachingSearchController.class);

    private SearchService searchService;

    @PostMapping("/auth/v1/search")
    public @ResponseBody SearchResponse search(@RequestBody @Valid SearchRequest searchRequest,
            @RequestHeader(value = "x-user-id", required = false) Long userId) throws Exception {
        log.debug("Search Request : {}", searchRequest);
        SearchResponse searchResponse = searchService.search(searchRequest, userId);
        return searchResponse;
    }
}
