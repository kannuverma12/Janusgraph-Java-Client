package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.response.dto.common.RecentSearch;

import java.util.List;

public interface RecentSearchesSerivce {

    public void recordSearches(String searchTerm, Long userId, EducationEntity educationEntity);

    public List<RecentSearch> getRecentSearchTerms(String query, Long userId, int size);

}
