package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.constant.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.constant.ExploreConstants.UPDATED_AT;

import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.MerchantArticle;
import com.paytm.digital.education.database.entity.MerchantStreamEntity;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.database.repository.MerchantStreamRepository;
import com.paytm.digital.education.explore.database.repository.MerchantArticleRepository;
import com.paytm.digital.education.explore.request.dto.articles.MerchantArticleRequest;
import com.paytm.digital.education.explore.response.dto.articles.ArticleResponseDTO;
import com.paytm.digital.education.explore.response.dto.articles.MerchantArticleResponse;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MerchantArticleServiceImpl {

    private static Logger log = LoggerFactory.getLogger(MerchantArticleServiceImpl.class);

    private MerchantArticleRepository merchantArticleRepository;
    private MerchantStreamRepository  merchantStreamRepository;
    private CommonMongoRepository commonMongoRepository;

    public MerchantArticleResponse saveArticle(
            MerchantArticleRequest merchantArticleRequest) {

        MerchantArticleResponse articleResponse = new MerchantArticleResponse();
        Exam exam = validateExam(merchantArticleRequest.getExamId());

        if (Objects.isNull(exam) && merchantArticleRequest.getExamId() != 0) {
            log.error("Invalid exam id : ", merchantArticleRequest.getExamId());
            return updateResponse(articleResponse, HttpStatus.NOT_FOUND,
                    "Invalid exam id", null);
        }

        Boolean validArticle = validateArticle(merchantArticleRequest.getArticleUrl());
        if (!validArticle) {
            log.error("Duplicate article found : ", merchantArticleRequest.getArticleUrl());
            return updateResponse(articleResponse, HttpStatus.CONFLICT,
                    "Duplicate article found", null);
        }
        MerchantArticle merchantArticle = new MerchantArticle();
        BeanUtils.copyProperties(merchantArticleRequest, merchantArticle);
        merchantArticle.setMerchantUpdatedAt(merchantArticleRequest.getUpdatedAt());
        Long paytmStreamId = getPaytmStreamId(merchantArticleRequest.getStream(),
                merchantArticleRequest.getMerchant());
        if (Objects.isNull(paytmStreamId)) {
            log.error("Stream does not exists. Article not saved.");
            return updateResponse(articleResponse,HttpStatus.NOT_FOUND,
                    "Stream does not exists. Article not saved.", null);
        }
        merchantArticle.setPaytmStreamId(paytmStreamId);
        try {
            MerchantArticle dbMerchantArticle =
                    merchantArticleRepository.save(merchantArticle);
            if (Objects.nonNull(dbMerchantArticle)) {
                ArticleResponseDTO articleResponseDTO = new ArticleResponseDTO();
                BeanUtils.copyProperties(dbMerchantArticle, articleResponseDTO);
                articleResponse = updateResponse(articleResponse,HttpStatus.OK,
                        "Article Saved successfully", articleResponseDTO);
            }
        } catch (Exception e) {
            log.error("Error saving Article : ", e);
            articleResponse = updateResponse(articleResponse,HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error saving Article", null);
        }

        return articleResponse;
    }

    private Boolean validateArticle(String articleUrl) {
        List<MerchantArticle> dbArticle = merchantArticleRepository.findAllByArticleUrl(articleUrl);
        if (CollectionUtils.isEmpty(dbArticle)) {
            return true;
        }
        return false;
    }

    private Exam validateExam(Long examId) {
        return commonMongoRepository.getEntityById(EXAM_ID, examId, Exam.class);
    }

    public List<MerchantArticle> getArticlesByStreamIds(List<Long> paytmStreamIds, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.Direction.DESC, UPDATED_AT);
        Page<MerchantArticle> pageResponse =
                merchantArticleRepository.findAllByPaytmStreamIdIn(paytmStreamIds, pageable);
        if (Objects.nonNull(pageResponse) && pageResponse.hasContent()) {
            return pageResponse.get().collect(Collectors.toList());
        }
        return null;
    }

    public List<MerchantArticle> getArticlesByExamId(long examId, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.Direction.DESC, UPDATED_AT);
        Page<MerchantArticle> pageResponse = merchantArticleRepository.findAllByExamId(examId, pageable);
        if (Objects.nonNull(pageResponse) && pageResponse.hasContent()) {
            return pageResponse.get().collect(Collectors.toList());
        }
        return null;
    }

    public List<MerchantArticle> getArticlesByExamIds(List<Long> examIds, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.Direction.DESC, UPDATED_AT);
        Page<MerchantArticle> pageResponse = merchantArticleRepository.findAllByExamIdIn(examIds, pageable);
        if (Objects.nonNull(pageResponse) && pageResponse.hasContent()) {
            return pageResponse.get().collect(Collectors.toList());
        }
        return null;
    }

    private MerchantArticleResponse updateResponse(MerchantArticleResponse
            articleResponse, HttpStatus status, String message,
            ArticleResponseDTO articleResponseDTO) {
        articleResponse.setStatus(status);
        articleResponse.setMessage(message);
        articleResponse.setArticleResponseDTO(articleResponseDTO);
        return articleResponse;
    }

    private Long getPaytmStreamId(String merchantStream, String merchant) {
        MerchantStreamEntity merchantStreamEntity =
                merchantStreamRepository.findByMerchantIdAndStream(merchant, merchantStream);
        if (Objects.nonNull(merchantStreamEntity)) {
            return merchantStreamEntity.getPaytmStreamId();
        }
        return null;
    }

}
