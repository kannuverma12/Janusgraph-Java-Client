package com.paytm.digital.education.explore.database.repository;

import com.paytm.digital.education.database.entity.MerchantArticle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantArticleRepository extends MongoRepository<MerchantArticle, String> {

    Page<MerchantArticle> findAllByExamId(Long examId, Pageable pageable);

    Page<MerchantArticle> findAllByExamIdIn(List<Long> examIds, Pageable pageable);

    Page<MerchantArticle> findAllByPaytmStreamIdIn(List<Long> paytmStreamId, Pageable pageable);

    List<MerchantArticle> findAllByArticleUrl(String articleUrl);
}
