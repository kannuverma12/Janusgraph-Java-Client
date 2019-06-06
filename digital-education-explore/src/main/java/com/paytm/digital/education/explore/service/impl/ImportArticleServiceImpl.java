package com.paytm.digital.education.explore.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.digital.education.explore.database.entity.Article;
import com.paytm.digital.education.explore.database.entity.FailedArticle;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.service.ImportDataService;
import com.paytm.digital.education.explore.service.helper.CampusEngagementHelper;
import com.paytm.digital.education.explore.utility.GoogleDriveUtil;
import com.paytm.digital.education.explore.xcel.model.XcelArticle;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mongodb.QueryOperators.OR;
import static com.paytm.digital.education.explore.constants.AWSConstants.S3_RELATIVE_PATH_FOR_ARTICLE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ARTICLES;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ARTICLE_DATA_RANGE_TEMPLATE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ARTICLE_HEADER_RANGE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ARTICLE_SHEET_ID;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ARTICLE_START_ROW;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ATTRIBUTES;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.DB_DATE_FORMAT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.FILE_DOWNLOAD_UPLOAD_FAILURE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.INVALID_INSTITUTE_IDS;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.XCEL_DATE_FORMAT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.XCEL_SUBMITTED_DATE_FORMAT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;

@Slf4j
@Service
@AllArgsConstructor
public class ImportArticleServiceImpl implements ImportDataService {
    private CommonMongoRepository  commonMongoRepository;
    private CampusEngagementHelper campusEngagementHelper;

    public boolean importData()
            throws IOException, GeneralSecurityException, ParseException {
        Map<String, Object> propertyMap = campusEngagementHelper.getCampusEngagementProperties();
        String sheetId = (String) propertyMap.get(ARTICLE_SHEET_ID);
        String headerRange = (String) propertyMap.get(ARTICLE_HEADER_RANGE);
        double startRow = (double) propertyMap.get(ARTICLE_START_ROW);
        String dataRangeTemplate = (String) propertyMap.get(ARTICLE_DATA_RANGE_TEMPLATE);
        List<Object> sheetArticleData = GoogleDriveUtil.getDataFromSheet(sheetId,
                MessageFormat.format(dataRangeTemplate, startRow), headerRange);
        if (Objects.nonNull(sheetArticleData)) {
            int totalNumberOfData = sheetArticleData.size();
            List<Object> failedArticles = new ArrayList<>();
            Map<Long, List<Article>> articleInstituteMap =
                    buildArticleInstituteMap(sheetArticleData, failedArticles);
            int insertedCount = addArticle(articleInstituteMap, failedArticles);
            double updatedCount = startRow + totalNumberOfData;
            propertyMap.put(ARTICLE_START_ROW, updatedCount);
            campusEngagementHelper
                    .updatePropertyMap(ATTRIBUTES + "." + ARTICLE_START_ROW, updatedCount);
            if (totalNumberOfData != insertedCount) {
                log.info("Number of the failed article data :"
                        + " {}", JsonUtils.toJson(totalNumberOfData - insertedCount));
                campusEngagementHelper.saveMultipleFailedData(failedArticles);
            }
        }
        return true;
    }

    /*
     ** Article related methods
     */
    private Map<Long, List<Article>> buildArticleInstituteMap(
            List<Object> xcelArticles, List<Object> failedArticleList) throws IOException,
            GeneralSecurityException, ParseException {
        Map<Long, List<Article>> articleInstituteMap = new HashMap<>();
        for (Object object : xcelArticles) {
            ObjectMapper mapper = new ObjectMapper();
            XcelArticle xcelArticle = mapper.convertValue(object, XcelArticle.class);
            Article article = new Article();
            Long instituteId = Long.parseLong(xcelArticle.getInstituteId());
            article.setInstituteId(instituteId);
            article.setArticleShortDescription(xcelArticle.getArticleShortDescription());
            article.setArticleTitle(xcelArticle.getArticleTitle());
            article.setStudentPaytmMobileNumber(xcelArticle.getStudentPaytmMobileNumber());
            article.setSubmittedBy(xcelArticle.getSubmittedBy());
            article.setCreatedAt(campusEngagementHelper.convertDateFormat(XCEL_DATE_FORMAT,
                    DB_DATE_FORMAT, xcelArticle.getTimestamp()));
            article.setSubmittedDate(
                    campusEngagementHelper.convertDateFormat(XCEL_SUBMITTED_DATE_FORMAT,
                            DB_DATE_FORMAT, xcelArticle.getSubmittedDate()));
            if (Objects.nonNull(xcelArticle.getArticlePdf())) {
                String pdfUrl = campusEngagementHelper.uploadFile(xcelArticle.getArticlePdf(), null,
                        instituteId, S3_RELATIVE_PATH_FOR_ARTICLE).getKey();
                if (Objects.nonNull(pdfUrl)) {
                    article.setArticlePdf(pdfUrl);
                } else {
                    article.setArticlePdf(xcelArticle.getArticlePdf());
                    FailedArticle failedArticle = new FailedArticle();
                    BeanUtils.copyProperties(article, failedArticle);
                    failedArticle.setReason(FILE_DOWNLOAD_UPLOAD_FAILURE);
                    failedArticle.setTimestamp(article.getCreatedAt());
                    failedArticle.setFailedDate(new Date());
                    failedArticleList.add(failedArticle);
                    continue;
                }
            }
            List<Article> instituteArticle =
                    articleInstituteMap.get(instituteId);
            if (Objects.isNull(instituteArticle)) {
                instituteArticle = new ArrayList<>();
            }
            instituteArticle.add(article);
            articleInstituteMap.put(instituteId, instituteArticle);
        }
        return articleInstituteMap;
    }

    private int addArticle(
            Map<Long, List<Article>> articleInstituteMap, List<Object> failedArticleList) {
        Set<Long> instituteIdSet = articleInstituteMap.keySet();
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(INSTITUTE_ID, new ArrayList<>(instituteIdSet));
        List<String> instituteFields = Arrays.asList(INSTITUTE_ID, ARTICLES);
        List<Institute> institutes = commonMongoRepository.findAll(queryObject, Institute.class,
                instituteFields, OR);
        Update update = new Update();
        int count = 0;
        for (Institute institute : institutes) {
            List<Article> articleList = institute.getArticles();
            if (articleList == null) {
                articleList = new ArrayList<>();
            }
            long instituteId = institute.getInstituteId();
            for (Article article :
                    articleInstituteMap.get(instituteId)) {
                articleList.add(article);
                count++;
            }
            update.set(ARTICLES, articleList);
            queryObject.put(INSTITUTE_ID, instituteId);
            commonMongoRepository.updateFirst(queryObject, instituteFields, update,
                    Institute.class);
            instituteIdSet.remove(instituteId);
        }
        if (!instituteIdSet.isEmpty()) {
            List<Object> failedData =
                    articleInstituteMap.entrySet().stream()
                            .filter(x -> instituteIdSet.contains(x.getKey()))
                            .flatMap(e1 -> e1.getValue().stream()
                                    .map(e2 -> convertToFailedObject(e2)))
                            .collect(Collectors.toList());
            if (!failedData.isEmpty()) {
                failedArticleList.addAll(failedData);
            }
        }
        return count;
    }

    private FailedArticle convertToFailedObject(Article a) {
        FailedArticle b = new FailedArticle();
        BeanUtils.copyProperties(a, b);
        b.setReason(INVALID_INSTITUTE_IDS);
        b.setTimestamp(a.getCreatedAt());
        b.setFailedDate(new Date());
        return b;
    }
}
