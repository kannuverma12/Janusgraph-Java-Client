package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.es.model.SearchHistory;
import com.paytm.digital.education.explore.response.dto.search.RecentSearch;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;

import java.util.List;

public interface RecentSearchesSerivce {

    void recordSearches(String searchTerm, Long userId, EducationEntity educationEntity);

    SearchResponse getRecentSearchTerms(String query, Long userId, int size);

    void ingestAudits(List<SearchHistory> searchHistories);

}
