package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.explore.response.dto.search.RecentSearch;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.service.RecentSearchesSerivce;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

@Controller
@AllArgsConstructor
@RequestMapping(EDUCATION_BASE_URL)
public class RecentSearchesController {

    private RecentSearchesSerivce recentSearchesSerivce;

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/recent_searches")
    public @ResponseBody SearchResponse autosuggest(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "size") int size,
            @RequestHeader(value = "x-user-id") Long userId) {
        return recentSearchesSerivce.getRecentSearchTerms(query, userId, size);
    }


}
