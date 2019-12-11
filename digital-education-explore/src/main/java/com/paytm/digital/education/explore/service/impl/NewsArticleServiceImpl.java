package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.constant.ExploreConstants.NEWS_ARTICLE_TITLE;

import com.paytm.digital.education.database.entity.MerchantArticle;
import com.paytm.digital.education.explore.response.dto.articles.NewsArticleResponse;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsArticleServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(NewsArticleServiceImpl.class);

    private final MerchantArticleServiceImpl merchantArticleService;

    @Value("${articles.default.count.institute}")
    private Integer defaultNoOfArticlesForInstitute;

    @Value("${articles.default.count.exam}")
    private Integer defaultNoOfArticlesForExam;

    public NewsArticleResponse getMerchantAritcleForExam(long examId, List<Long> streamIds) {
        try {
            List<MerchantArticle> articlesToReturn =
                    merchantArticleService.getArticlesByExamId(examId, defaultNoOfArticlesForExam);
            if (CollectionUtils.isEmpty(articlesToReturn)) {
                articlesToReturn = new ArrayList<>();
            }
            int articleSize = articlesToReturn.size();
            if (articleSize < defaultNoOfArticlesForExam) {
                int remainingArticles = defaultNoOfArticlesForExam - articleSize;
                if (!CollectionUtils.isEmpty(streamIds)) {
                    streamIds = streamIds.size() > 2 ? streamIds.subList(0, 2) : streamIds;
                    List<MerchantArticle> streamWiseArticles =
                            merchantArticleService
                                    .getArticlesByStreamIds(streamIds, remainingArticles);
                    if (!CollectionUtils.isEmpty(streamWiseArticles)) {
                        articlesToReturn.addAll(streamWiseArticles);
                    }
                }
                return NewsArticleResponse.builder().title(NEWS_ARTICLE_TITLE)
                        .data(articlesToReturn).build();
            }
            return getByStreamIdsOnly(streamIds, defaultNoOfArticlesForExam);
        } catch (Exception ex) {
            log.error("Exception caught while getting merchant article for exam. ExamId : ", ex,
                    examId);
        }
        return null;
    }


    public NewsArticleResponse getMerchantArticleForInstitute(List<Long> examIds,
            List<Long> streamIds) {
        try {
            if (!CollectionUtils.isEmpty(examIds)) {
                List<MerchantArticle> articlesToReturn = merchantArticleService
                        .getArticlesByExamIds(examIds, defaultNoOfArticlesForExam);
                if (CollectionUtils.isEmpty(articlesToReturn)) {
                    articlesToReturn = new ArrayList<>();
                }
                int articleSize = articlesToReturn.size();
                if (articleSize < defaultNoOfArticlesForInstitute) {
                    int remainingArticles = defaultNoOfArticlesForExam - articleSize;
                    List<MerchantArticle> streamWiseArticles =
                            merchantArticleService
                                    .getArticlesByStreamIds(streamIds, remainingArticles);
                    if (!CollectionUtils.isEmpty(streamWiseArticles)) {
                        articlesToReturn.addAll(streamWiseArticles);
                    }
                }
                return NewsArticleResponse.builder().title(NEWS_ARTICLE_TITLE)
                        .data(articlesToReturn).build();
            }
            return getByStreamIdsOnly(streamIds, defaultNoOfArticlesForInstitute);
        } catch (Exception ex) {
            log.error(
                    "Exception caught while getting merchant article for institute. Exam Ids : {}, StreamIds : {}",
                    ex, examIds, streamIds);
        }
        return null;
    }

    private NewsArticleResponse getByStreamIdsOnly(List<Long> streamIds, int limit) {
        if (!CollectionUtils.isEmpty(streamIds)) {
            List<MerchantArticle> articleList =
                    merchantArticleService.getArticlesByStreamIds(streamIds, limit);
            if (!CollectionUtils.isEmpty(articleList)) {
                return NewsArticleResponse.builder().title(NEWS_ARTICLE_TITLE)
                        .data(articleList).build();
            }
        }
        return null;
    }
}
