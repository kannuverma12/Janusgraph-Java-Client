package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends MongoRepository<Page, String> {
    Page getPageByName(String pageName);
}
