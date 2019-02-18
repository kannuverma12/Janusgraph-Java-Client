package com.paytm.digital.education.explore.database.repository;

import com.paytm.digital.education.explore.database.entity.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends MongoRepository<Page, String> {
    Page getPageByName(String pageName);
}
