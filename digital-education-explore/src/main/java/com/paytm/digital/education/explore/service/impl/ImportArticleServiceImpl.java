package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.config.AwsConfig;
import com.paytm.digital.education.config.GoogleConfig;
import com.paytm.digital.education.database.entity.FailedData;
import com.paytm.digital.education.database.repository.FailedDataRepository;
import com.paytm.digital.education.explore.database.entity.Article;
import com.paytm.digital.education.explore.database.entity.CampusEngagement;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.service.ImportDataService;
import com.paytm.digital.education.explore.service.helper.CampusEngagementHelper;
import com.paytm.digital.education.utility.GoogleDriveUtil;
import com.paytm.digital.education.explore.xcel.model.XcelArticle;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.digital.education.utility.UploadUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mongodb.QueryOperators.OR;
import static com.paytm.digital.education.explore.constants.AWSConstants.S3_RELATIVE_PATH_FOR_ARTICLE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ARTICLES;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ARTICLE_DATA_RANGE_TEMPLATE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ARTICLE_HEADER_RANGE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ARTICLE_SHEET_ID;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ARTICLE_START_ROW;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ATTRIBUTES;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.COMPONENT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.DB_DATE_FORMAT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.FILE_DOWNLOAD_UPLOAD_FAILURE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.HAS_IMPORTED;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.INVALID_INSTITUTE_IDS;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.IS_IMPORTABLE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.TYPE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.XCEL_DATE_FORMAT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.XCEL_SUBMITTED_DATE_FORMAT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;

@Slf4j
@Service
@AllArgsConstructor
public class ImportArticleServiceImpl implements ImportDataService {
    private CommonMongoRepository  commonMongoRepository;
    private CampusEngagementHelper campusEngagementHelper;
    private UploadUtil             uploadUtil;
    private FailedDataRepository   failedDataRepository;

    /*
     ** Import the data from the spreadsheet
     */
    public boolean importData(boolean isReimportOnly)
            throws IOException, GeneralSecurityException, ParseException {
        List<Object> sheetArticleData = null;
        double startRow = 0;
        if (!isReimportOnly) {
            Map<String, Object> propertyMap =
                    campusEngagementHelper.getCampusEngagementProperties();
            String sheetId = (String) propertyMap.get(ARTICLE_SHEET_ID);
            String headerRange = (String) propertyMap.get(ARTICLE_HEADER_RANGE);
            startRow = (double) propertyMap.get(ARTICLE_START_ROW);
            String dataRangeTemplate = (String) propertyMap.get(ARTICLE_DATA_RANGE_TEMPLATE);
            sheetArticleData = GoogleDriveUtil.getDataFromSheet(sheetId,
                    MessageFormat.format(dataRangeTemplate, startRow), headerRange,
                    GoogleConfig.getCampusCredentialFileName(),
                    GoogleConfig.getExploreCredentialFolderPath());
        }
        List<XcelArticle> xcelArticles = new ArrayList<>();
        List<Long> instituteIds = new ArrayList<>();
        List<Object> failedDataList = new ArrayList<>();
        List<XcelArticle> previousFailedArticleList = getAllFailedData(instituteIds);
        if (Objects.nonNull(sheetArticleData)) {
            xcelArticles = sheetArticleData.stream()
                    .map(e2 -> JsonUtils.convertValue(e2, XcelArticle.class))
                    .peek(xcelArticle -> instituteIds
                            .add(xcelArticle.getInstituteId()))
                    .collect(Collectors.toList());
        }
        List<Long> validInstituteIdList = new ArrayList<>();
        Map<Long, List<Article>> articleMap = new HashMap<>();
        if (!instituteIds.isEmpty()) {
            Map<String, Object> queryObject = new HashMap<>();
            queryObject.put(INSTITUTE_ID, new ArrayList<>(instituteIds));
            List<CampusEngagement> campusEngagementList = commonMongoRepository.findAll(queryObject,
                    CampusEngagement.class,
                    Arrays.asList(ARTICLES, INSTITUTE_ID), OR);
            if (Objects.nonNull(campusEngagementList)) {
                articleMap = campusEngagementList.stream()
                        .collect(HashMap::new, (m, v) -> m.put(v.getInstituteId(), v.getArticles()),
                                HashMap::putAll);
            }
            List<Institute> validInstitutes =
                    commonMongoRepository
                            .findAll(queryObject, Institute.class, Arrays.asList(INSTITUTE_ID), OR);
            validInstituteIdList = validInstitutes.stream().map(c -> c.getInstituteId())
                    .collect(Collectors.toList());
        }
        if (!previousFailedArticleList.isEmpty()) {
            buildInstituteArticleMap(previousFailedArticleList, validInstituteIdList,
                    articleMap, failedDataList);
            campusEngagementHelper.updateReimportStatus(ARTICLES, EXPLORE_COMPONENT);
        }
        if (!xcelArticles.isEmpty()) {
            buildInstituteArticleMap(xcelArticles, validInstituteIdList,
                    articleMap, failedDataList);
            saveArticles(articleMap);
            campusEngagementHelper.updatePropertyMap(ATTRIBUTES + '.' + ARTICLE_START_ROW,
                    startRow + sheetArticleData.size());
        }
        if (!failedDataList.isEmpty()) {
            failedDataRepository.saveAll(failedDataList);
        }
        return true;
    }


    /*
     ** Build the institute Article Map when input data is from spreadsheet
     */
    private Map<Long, List<Article>> buildInstituteArticleMap(
            List<XcelArticle> xcelArticles, List<Long> validInstituteIdList, Map<Long,
            List<Article>> articleInstituteMap, List<Object> failedArticleList)
            throws ParseException {
        for (XcelArticle xcelArticle : xcelArticles) {
            Long instituteId = xcelArticle.getInstituteId();
            if (Objects.nonNull(instituteId) && validInstituteIdList.contains(instituteId)) {
                List<Article> existingArticles =
                        articleInstituteMap.get(instituteId);
                if (Objects.isNull(existingArticles)) {
                    existingArticles = new ArrayList<>();
                }
                Article article = new Article();
                article.setInstituteId(instituteId);
                article.setArticleShortDescription(xcelArticle.getArticleShortDescription());
                article.setArticleTitle(xcelArticle.getArticleTitle());
                article.setStudentPaytmMobileNumber(xcelArticle.getStudentPaytmMobileNumber());
                article.setSubmittedBy(xcelArticle.getSubmittedBy());
                article.setEmailAddress(xcelArticle.getEmailAddress());
                article.setCreatedAt(campusEngagementHelper.convertDateFormat(XCEL_DATE_FORMAT,
                        DB_DATE_FORMAT, xcelArticle.getTimestamp()));
                article.setSubmittedDate(
                        campusEngagementHelper.convertDateFormat(XCEL_SUBMITTED_DATE_FORMAT,
                                DB_DATE_FORMAT, xcelArticle.getSubmittedDate()));
                if (StringUtils.isNotBlank(xcelArticle.getArticlePdf())) {
                    if (!setDocsFields(article, xcelArticle.getArticlePdf())) {
                        campusEngagementHelper
                                .addToFailedList(xcelArticle, FILE_DOWNLOAD_UPLOAD_FAILURE, true,
                                        failedArticleList,
                                        EXPLORE_COMPONENT, ARTICLES);
                        continue;
                    }
                }
                existingArticles.add(article);
                articleInstituteMap.put(instituteId, existingArticles);

            } else {
                // Failure case handling invalid institute Id
                campusEngagementHelper
                        .addToFailedList(xcelArticle, INVALID_INSTITUTE_IDS, false,
                                failedArticleList,
                                EXPLORE_COMPONENT, ARTICLES);
            }
        }
        return articleInstituteMap;
    }

    /*
     ** Set the docs url when successfully uploaded and return true else false
     */
    private boolean setDocsFields(Article article, String pdfUrl) {
        String relativeUrl = uploadUtil.uploadFile(pdfUrl, null,
                article.getInstituteId(), S3_RELATIVE_PATH_FOR_ARTICLE,
                AwsConfig.getS3ExploreBucketName(), GoogleConfig.getCampusCredentialFileName(),
                GoogleConfig.getExploreCredentialFolderPath())
                .getKey();
        if (Objects.nonNull(relativeUrl)) {
            article.setArticlePdf(relativeUrl);
            return true;
        } else {
            return false;
        }
    }

    private void saveArticles(
            Map<Long, List<Article>> articleMap) {
        for (Map.Entry<Long, List<Article>> entry : articleMap
                .entrySet()) {
            if (Objects.nonNull(entry.getValue())) {
                Map<String, Object> queryObject = new HashMap<>();
                queryObject.put(INSTITUTE_ID, entry.getKey());
                List<String> fields = Arrays.asList(INSTITUTE_ID, ARTICLES);
                Update update = new Update();
                update.set(INSTITUTE_ID, entry.getKey());
                update.set(ARTICLES, entry.getValue());
                commonMongoRepository.upsertData(queryObject, fields, update,
                        CampusEngagement.class);
            }
        }
    }

    private List<XcelArticle> getAllFailedData(List<Long> instituteIds) {
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(COMPONENT, EXPLORE_COMPONENT);
        queryObject.put(TYPE, ARTICLES);
        queryObject.put(HAS_IMPORTED, false);
        queryObject.put(IS_IMPORTABLE, true);
        List<FailedData> failedArticleList = failedDataRepository.findAll(queryObject);
        List<XcelArticle> failedData =
                failedArticleList.stream().map(c -> JsonUtils.convertValue(c.getData(),
                        XcelArticle.class)).peek(article -> instituteIds
                        .add(article.getInstituteId())).collect(Collectors.toList());
        return failedData;
    }
}
