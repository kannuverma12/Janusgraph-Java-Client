//package com.paytm.digital.education.explore.scheduler;
//
//import com.paytm.digital.education.explore.constants.ExploreConstants;
//import com.paytm.digital.education.explore.enums.EducationEntity;
//import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
//import com.paytm.digital.education.explore.response.dto.search.FilterData;
//import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
//import com.paytm.digital.education.explore.response.dto.search.TermFilterData;
//import com.paytm.digital.education.explore.service.impl.RecentSearchServiceImpl;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.util.CollectionUtils;
//
//import java.io.IOException;
//import java.util.Objects;
//import java.util.concurrent.TimeoutException;
//
//@EnableScheduling
//@Configuration
//@Slf4j
//public class RecentsLimitCheckScheduler {
//
//    private RecentSearchServiceImpl recentSearchService;
//
//    public void extractUserId() {
//
//        SearchResponse searchResponse = getAggregationsFromElastic();
//
//    }
//
//    //    private Long extractUserId(SearchResponse searchResponse) {
//    //        //
//    //        //      for(FilterData filterData : searchResponse.getFilters()){
//    //        /         if(ExploreConstants.SEARCH_HISTORY_USERID.equals(filterData.getName())){
//    //        //                TermFilterData termFilterData = (TermFilterData)filterData;
//    //        //                if(!CollectionUtils.isEmpty(termFilterData.getBuckets())){
//    //        //                    termFilterData.getBuckets().get(0)
//    //        //                }
//    //        //            }
//    //        //        }
//    //        //
//    //        //     TermFilterData termFilterData = (TermFilterData)searchResponse.getFilters();
//    //    }
//
//    private SearchResponse getAggregationsFromElastic() {
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.setFetchFilter(true);
//        searchRequest.setLimit(0);
//        searchRequest.setEntity(EducationEntity.RECENT_SEARCHES);
//
//        try {
//            SearchResponse searchResponse = recentSearchService.search(searchRequest);
//            return searchResponse;
//        } catch (IOException e) {
//            log.error("Could not connect to elasticsearch :{}", e.getLocalizedMessage());
//        } catch (TimeoutException e) {
//            log.error("Could not connect to elasticsearch :{}", e.getLocalizedMessage());
//        }
//        return null;
//    }
//
//
//}
