package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.service.RecentsSerivce;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;

@Controller
@AllArgsConstructor
@RequestMapping(EDUCATION_BASE_URL)
public class RecentsController {

    private RecentsSerivce recentsSerivce;

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/recent_searches")
    public @ResponseBody SearchResponse recents(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size,
            @RequestParam(value = "entity", required = false) List<EducationEntity> entities,
            @RequestHeader(value = "x-user-id") Long userId) {
        return recentsSerivce.getRecentSearchTerms(query, userId, size, entities);
    }
}
