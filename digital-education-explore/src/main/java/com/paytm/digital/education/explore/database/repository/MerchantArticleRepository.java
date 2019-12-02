package com.paytm.digital.education.explore.database.repository;

import com.paytm.digital.education.database.entity.MerchantArticle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantArticleRepository extends MongoRepository<MerchantArticle, String> {

}
