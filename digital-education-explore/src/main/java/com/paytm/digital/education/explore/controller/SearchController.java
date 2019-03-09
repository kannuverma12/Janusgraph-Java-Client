package com.paytm.digital.education.explore.controller;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.service.impl.SearchServiceImpl;
import com.paytm.digital.education.explore.validators.SearchRequestValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(EDUCATION_BASE_URL)
public class SearchController {
    private SearchRequestValidator searchRequestValidator;
    private SearchServiceImpl      searchServiceImpl;

    @PostMapping("/auth/v1/search")
    public @ResponseBody SearchResponse search(@RequestBody SearchRequest searchRequest,
            @RequestHeader(value = "x-user-id", required = false) Long userId) throws
            Exception {
        searchRequestValidator.validate(searchRequest);
        SearchResponse searchResponse = searchServiceImpl.search(searchRequest, userId);
        return searchResponse;
    }
}
