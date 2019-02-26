package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class SearchServiceImpl {

    private InstituteSearchServiceImpl instituteSearchService;

    public SearchResponse search(SearchRequest searchRequest) throws Exception {
        SearchResponse response = handler(searchRequest.getEntity()).search(searchRequest);
        log.debug("Search Response : {}", JsonUtils.toJson(response));
        return response;
    }

    private AbstractSearchServiceImpl handler(EducationEntity educationEntity) {
        switch (educationEntity) {
            case INSTITUTE:
                return instituteSearchService;
            default:
                throw new RuntimeException("Invalid entity requested.");
        }
    }
}
