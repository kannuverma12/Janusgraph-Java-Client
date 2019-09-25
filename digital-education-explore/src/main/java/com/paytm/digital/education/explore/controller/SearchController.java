package com.paytm.digital.education.explore.controller;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ENTITY_ID;
import static com.paytm.digital.education.explore.enums.Client.WEB;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.enums.Client;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.response.dto.suggest.AutoSuggestResponse;
import com.paytm.digital.education.explore.service.impl.SearchServiceImpl;
import com.paytm.digital.education.explore.validators.CourseSearchValidator;
import com.paytm.digital.education.explore.validators.ExploreValidator;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
@RequestMapping(EDUCATION_BASE_URL)
public class SearchController {

    private static Logger log = LoggerFactory.getLogger(SearchController.class);

    private SearchServiceImpl searchServiceImpl;
    private ExploreValidator  exploreValidator;

    @PostMapping("/auth/v1/search")
    public @ResponseBody SearchResponse search(@RequestBody SearchRequest searchRequest,
            @RequestHeader(value = "x-user-id", required = false) Long userId,
            @RequestHeader(value = "fe_client", defaultValue = "WEB",
                    required = false) Client client) throws Exception {
        log.info("Search Request : {}", JsonUtils.toJson(searchRequest));
        log.info("Client : " + client);
        exploreValidator.validateAndThrowException(searchRequest);
        if (searchRequest.getEntity().equals(EducationEntity.COURSE)) {
            CourseSearchValidator.validateRequest(searchRequest);
        }

        SearchResponse searchResponse = searchServiceImpl.search(searchRequest, userId, client);
        return searchResponse;
    }

    @PostMapping("/auth/v1/institute/search")
    public @ResponseBody AutoSuggestResponse instituteSearch(@RequestBody SearchRequest searchRequest,
            @RequestHeader(value = "x-user-id", required = false) Long userId) throws Exception {
        log.info("Search Request : {}", JsonUtils.toJson(searchRequest));
        exploreValidator.validateAndThrowException(searchRequest);
        if (searchRequest.getEntity().equals(EducationEntity.COURSE)) {
            CourseSearchValidator.validateRequest(searchRequest);
        }

        AutoSuggestResponse searchResponse = searchServiceImpl.instituteSearch(searchRequest);
        return searchResponse;
    }
}
