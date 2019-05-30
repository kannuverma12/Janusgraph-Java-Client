package com.paytm.digital.education.explore.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.digital.education.explore.database.entity.Article;
import com.paytm.digital.education.explore.database.entity.CampusEvent;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.service.ImportDataService;
import com.paytm.digital.education.explore.service.helper.CampusEngagementHelper;
import com.paytm.digital.education.explore.utility.CommonUtil;
import com.paytm.digital.education.explore.utility.GoogleDriveUtil;
import com.paytm.digital.education.explore.xcel.model.XcelArticle;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.mongodb.QueryOperators.OR;
import static com.paytm.digital.education.explore.constants.AWSConstants.S3_BUCKET_PATH;
import static com.paytm.digital.education.explore.constants.AWSConstants.S3_PATH_FOR_ARTICLE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ARTICLES;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ARTICLE_DATA_RANGE_TEMPLATE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ARTICLE_HEADER_RANGE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ARTICLE_SHEET_ID;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ARTICLE_START_ROW;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ATTRIBUTES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;

@Slf4j
@Service
@AllArgsConstructor
public class ImportArticleServiceImpl implements ImportDataService {
    private CommonMongoRepository  commonMongoRepository;
    private CampusEngagementHelper campusEngagementHelper;

    public Map<Long, List<CampusEvent>> importData()
            throws IOException, GeneralSecurityException {
        Map<String, Object> propertyMap = campusEngagementHelper.getCampusEngagementProperties();
        String sheetId = (String) propertyMap.get(ARTICLE_SHEET_ID);
        String headerRange = (String) propertyMap.get(ARTICLE_HEADER_RANGE);
        double startRow = (double) propertyMap.get(ARTICLE_START_ROW);
        String dataRangeTemplate = (String) propertyMap.get(ARTICLE_DATA_RANGE_TEMPLATE);
        List<Object> sheetArticleData = GoogleDriveUtil.getDataFromSheet(sheetId,
                MessageFormat.format(dataRangeTemplate, startRow), headerRange);
        if (Objects.nonNull(sheetArticleData)) {
            Map<Long, List<Article>> articleInstituteMap =
                    buildArticleInstituteMap(sheetArticleData);
            int insertedCount = addArticle(articleInstituteMap);
            if (insertedCount > 0) {
                double updatedCount = startRow + insertedCount;
                propertyMap.put(ARTICLE_START_ROW, updatedCount);
                campusEngagementHelper
                        .updatePropertyMap(ATTRIBUTES + "." + ARTICLE_START_ROW, updatedCount);
            }
        }
        return null;
    }

    /*
     ** Article related methods
     */
    private Map<Long, List<Article>> buildArticleInstituteMap(
            List<Object> xcelArticles) throws IOException,
            GeneralSecurityException {
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
            if (Objects.nonNull(xcelArticle.getArticlePdf())) {
                article.setArticlePdf(campusEngagementHelper.uploadToS3(xcelArticle.getArticlePdf(), null,
                        instituteId,
                        S3_BUCKET_PATH, S3_PATH_FOR_ARTICLE).getKey());
            }
            article.setCreatedAt(xcelArticle.getTimestamp());
            article.setSubmittedDate(xcelArticle.getSubmittedDate());
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
            Map<Long, List<Article>> articleInstituteMap) {
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
            for (Article article :
                    articleInstituteMap.get(institute.getInstituteId())) {
                articleList.add(article);
                count++;
            }
            update.set(ARTICLES, articleList);
            queryObject.put(INSTITUTE_ID, institute.getInstituteId());
            commonMongoRepository.updateFirst(queryObject, instituteFields, update,
                    Institute.class);
        }
        return count;
    }
}
