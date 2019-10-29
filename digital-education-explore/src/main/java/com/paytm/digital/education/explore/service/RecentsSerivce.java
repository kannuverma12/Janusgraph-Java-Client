package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.explore.es.model.SearchHistoryEsDoc;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;

import java.util.List;

public interface RecentsSerivce {

    void recordSearches(String searchTerm, Long userId, EducationEntity educationEntity);

    SearchResponse getRecentSearchTerms(String query, Long userId, int size, List<EducationEntity> entities);

    void ingestAudits(List<SearchHistoryEsDoc> searchHistoryEsDoc);
}
