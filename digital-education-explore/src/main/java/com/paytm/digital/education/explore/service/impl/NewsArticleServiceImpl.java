package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.constant.ExploreConstants.NEWS_ARTICLE_TITLE;

import com.paytm.digital.education.database.entity.MerchantArticle;
import com.paytm.digital.education.explore.response.dto.articles.NewsArticleData;
import com.paytm.digital.education.explore.response.dto.articles.NewsArticleResponse;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        List<MerchantArticle> articlesToReturn = null;
        try {
            articlesToReturn =
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
            }
            return buildNewsArticleResponse(articlesToReturn);
        } catch (Exception ex) {
            log.error("Exception caught while getting merchant article for exam. ExamId : {}, streamIds : {}", ex,
                    examId, streamIds);
        }
        return null;
    }


    public NewsArticleResponse getMerchantArticleForInstitute(List<Long> examIds,
            List<Long> streamIds) {
        List<MerchantArticle> articlesToReturn = null;
        try {
            if (!CollectionUtils.isEmpty(examIds)) {
                articlesToReturn = merchantArticleService
                        .getArticlesByExamIds(examIds, defaultNoOfArticlesForExam);
                if (CollectionUtils.isEmpty(articlesToReturn)) {
                    articlesToReturn = new ArrayList<>();
                }
                int articleSize = articlesToReturn.size();
                if (articleSize < defaultNoOfArticlesForInstitute) {
                    int remainingArticles = defaultNoOfArticlesForInstitute - articleSize;
                    List<MerchantArticle> streamWiseArticles =
                            merchantArticleService
                                    .getArticlesByStreamIds(streamIds, remainingArticles);
                    if (!CollectionUtils.isEmpty(streamWiseArticles)) {
                        articlesToReturn.addAll(streamWiseArticles);
                    }
                }
            }
            return buildNewsArticleResponse(articlesToReturn);
        } catch (Exception ex) {
            log.error(
                    "Exception caught while getting merchant article for institute. Exam Ids : {}, StreamIds : {}",
                    ex, examIds, streamIds);
        }
        return null;
    }

    private NewsArticleResponse buildNewsArticleResponse(List<MerchantArticle> articleList) {
        if (!CollectionUtils.isEmpty(articleList)) {
            Set<String> articleUrls = new HashSet<>();
            List<NewsArticleData> articleData = articleList.stream()
                    .filter(article -> !articleUrls.contains(article.getArticleUrl()))
                    .peek(article -> articleUrls.add(article.getArticleUrl()))
                    .map(this::convertToArticleResponse)
                    .collect(Collectors.toList());
            Collections.sort(articleData,
                    Comparator.comparing(NewsArticleData::getUpdatedAt).reversed());
            return NewsArticleResponse.builder().title(NEWS_ARTICLE_TITLE)
                    .data(articleData).build();
        }
        return null;
    }

    private NewsArticleData convertToArticleResponse(MerchantArticle merchantArticle) {
        NewsArticleData newsArticleData = new NewsArticleData();
        BeanUtils.copyProperties(merchantArticle, newsArticleData);
        return newsArticleData;
    }
}
