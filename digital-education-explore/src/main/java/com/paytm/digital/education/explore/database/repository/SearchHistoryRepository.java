package com.paytm.digital.education.explore.database.repository;

import com.paytm.digital.education.explore.database.entity.SearchHistory;
import com.paytm.digital.education.explore.enums.ESIngestionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends MongoRepository<SearchHistory, String> {

    public List<SearchHistory> findManyBystatus(ESIngestionStatus status);

}
