package com.paytm.digital.education.admin.service.impl;

import com.paytm.digital.education.admin.request.AmbassadorRequest;
import com.paytm.digital.education.admin.request.ArticleRequest;
import com.paytm.digital.education.admin.request.EventRequest;
import com.paytm.digital.education.admin.response.CampusAdminResponse;
import com.paytm.digital.education.admin.service.CampusAdminService;
import com.paytm.digital.education.config.AwsConfig;
import com.paytm.digital.education.config.GoogleConfig;
import com.paytm.digital.education.database.entity.Article;
import com.paytm.digital.education.database.entity.CampusAmbassador;
import com.paytm.digital.education.database.entity.CampusEngagement;
import com.paytm.digital.education.database.entity.CampusEvent;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.service.helper.CampusEngagementHelper;
import com.paytm.digital.education.explore.xcel.model.XcelArticle;
import com.paytm.digital.education.explore.xcel.model.XcelCampusAmbassador;
import com.paytm.digital.education.explore.xcel.model.XcelEvent;
import com.paytm.digital.education.utility.UploadUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.mongodb.QueryOperators.EXISTS;
import static com.mongodb.QueryOperators.NE;
import static com.paytm.digital.education.constant.ExploreConstants.DIRECTORY_SEPARATOR_SLASH;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.constants.AWSConstants.S3_RELATIVE_PATH_FOR_AMBASSADOR;
import static com.paytm.digital.education.explore.constants.AWSConstants.S3_RELATIVE_PATH_FOR_ARTICLE;
import static com.paytm.digital.education.explore.constants.AWSConstants.S3_RELATIVE_PATH_FOR_EVENT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ARTICLES;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.CAMPUS_AMBASSADORS;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.DB_DATE_FORMAT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.DRIVE_URL;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.EVENTS;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.IMAGE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.VIDEO;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.XCEL_DATE_FORMAT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.XCEL_SUBMITTED_DATE_FORMAT;

@Service
@AllArgsConstructor
public class CampusAdminServiceImpl implements CampusAdminService {
    private static final Logger log = LoggerFactory.getLogger(CampusAdminServiceImpl.class);

    private CommonMongoRepository  commonMongoRepository;
    private CampusEngagementHelper campusEngagementHelper;
    private UploadUtil             uploadUtil;
    private MongoOperations        mongoOperations;

    @Override
    public CampusAdminResponse addAmbassadors(AmbassadorRequest ambassador) {
        CampusAdminResponse campusAdminResponse = new CampusAdminResponse();

        if (Objects.nonNull(ambassador)) {
            Long instituteId = ambassador.getInstituteId();
            if (Objects.nonNull(instituteId) && validInstitute(instituteId)) {

                String mobileNumber = ambassador.getPaytmMobileNumber();
                if (StringUtils.isNotBlank(mobileNumber) && validMobileNumber(mobileNumber)) {
                    Map<String, Object> queryObject = new HashMap<>();
                    queryObject.put(INSTITUTE_ID, instituteId);
                    CampusEngagement campusEngagement = commonMongoRepository
                            .getEntityById(INSTITUTE_ID, instituteId, CampusEngagement.class);
                    Map<String, CampusAmbassador> ambassadorMap;
                    CampusAmbassador campusAmbassador;

                    if (Objects.nonNull(campusEngagement)) {
                        ambassadorMap = campusEngagement.getCampusAmbassadors();

                        if (Objects.nonNull(ambassadorMap)) {
                            if (StringUtils.isNotBlank(mobileNumber)) {
                                campusAmbassador = ambassadorMap.get(mobileNumber.trim());
                                if (Objects.isNull(campusAmbassador)) {
                                    campusAmbassador = new CampusAmbassador();
                                }
                            } else {
                                campusAdminResponse.setMessage("Ambassador not added");
                                campusAdminResponse.setError("Please provide mobile number");
                                return campusAdminResponse;
                            }
                        } else {
                            ambassadorMap = new HashMap<>();
                            campusAmbassador = new CampusAmbassador();
                        }
                    } else {
                        ambassadorMap = new HashMap<>();
                        campusAmbassador = new CampusAmbassador();
                    }

                    if (StringUtils.isNotBlank(ambassador.getName())) {
                        campusAmbassador.setName(ambassador.getName());
                    }
                    if (StringUtils.isNotBlank(ambassador.getCourse())) {
                        campusAmbassador.setCourse(ambassador.getCourse());
                    }
                    campusAmbassador.setInstituteId(instituteId);
                    campusAmbassador.setPaytmMobileNumber(mobileNumber.trim());

                    if (StringUtils.isNotBlank(ambassador.getYearAndBatch())) {
                        campusAmbassador.setYearAndBatch(ambassador.getYearAndBatch());
                    }
                    if (StringUtils.isNotBlank(ambassador.getEmailAddress())) {
                        campusAmbassador.setEmailAddress(ambassador.getEmailAddress());
                    }
                    try {
                        campusAmbassador.setCreatedAt(
                                campusEngagementHelper.convertDateFormat(XCEL_DATE_FORMAT,
                                        DB_DATE_FORMAT, ambassador.getTimestamp()));
                    } catch (Exception e) {
                        log.error("Error parsing date : {} ", e.getMessage());
                    }

                    campusAmbassador.setLastUpdated(campusAmbassador.getCreatedAt());

                    if (StringUtils.isNotBlank(ambassador.getImage())) {
                        if (ambassador.getImage().startsWith(DRIVE_URL)) {
                            log.info("Going to upload image to s3");
                            if (setMediaFields(campusAmbassador, ambassador.getImage())) {
                                log.info("Image Upload successful.");
                            } else {
                                log.info("Image Upload failed.");
                            }
                        } else {
                            log.info("Image already uploaded. Setting the db path.");
                            campusAmbassador.setImageUrl(ambassador.getImage());
                        }
                    }

                    ambassadorMap.put(mobileNumber.trim(), campusAmbassador);

                    Map<String, Object> queryObject1 = new HashMap<>();
                    queryObject1.put(INSTITUTE_ID, instituteId);
                    List<String> fields = Arrays.asList(INSTITUTE_ID, CAMPUS_AMBASSADORS);
                    Update update = new Update();
                    update.set(INSTITUTE_ID, instituteId);
                    update.set(CAMPUS_AMBASSADORS, ambassadorMap);
                    commonMongoRepository.upsertData(queryObject1, fields, update,
                            CampusEngagement.class);

                    campusAdminResponse.setEntity(String.valueOf(instituteId));
                    campusAdminResponse.setMessage("Ambassador added successfully");

                } else {
                    campusAdminResponse.setMessage("Ambassador not added");
                    campusAdminResponse.setError("Invalid mobile number");
                    return campusAdminResponse;
                }
            } else {
                campusAdminResponse.setMessage("Ambassador not added");
                campusAdminResponse.setError("Invalid Institute Id");
            }
        }

        return campusAdminResponse;
    }

    @Override public List<XcelCampusAmbassador> getAllAmbassadors() {

        List<XcelCampusAmbassador> ambassadorList = new ArrayList<>();
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put(EXISTS, true);
        queryMap.put(NE, null);

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put(CAMPUS_AMBASSADORS, queryMap);

        List<CampusEngagement> campusEngagementList =
                mongoOperations.findAll(CampusEngagement.class);

        for (CampusEngagement campusEngagement : campusEngagementList) {
            if (Objects.nonNull(campusEngagement.getCampusAmbassadors())) {
                for (CampusAmbassador campusAmbassador : campusEngagement.getCampusAmbassadors()
                        .values()) {
                    XcelCampusAmbassador xcelCampusAmbassador = new XcelCampusAmbassador();
                    BeanUtils.copyProperties(campusAmbassador, xcelCampusAmbassador);
                    xcelCampusAmbassador.setImage(campusAmbassador.getImageUrl());
                    ambassadorList.add(xcelCampusAmbassador);
                }
            }
        }
        return ambassadorList;
    }

    @Override
    public CampusAdminResponse addArticles(ArticleRequest xcelArticle) {
        CampusAdminResponse campusAdminResponse = new CampusAdminResponse();

        if (Objects.nonNull(xcelArticle)) {
            Long instituteId = xcelArticle.getInstituteId();
            if (Objects.nonNull(instituteId) && validInstitute(instituteId)) {

                String mobileNumber = xcelArticle.getStudentPaytmMobileNumber();
                if (!(StringUtils.isNotBlank(mobileNumber) && validMobileNumber(mobileNumber))) {
                    campusAdminResponse.setMessage("Article not added");
                    campusAdminResponse.setError("Invalid mobile number");
                    return campusAdminResponse;
                }

                Map<String, Object> queryObject = new HashMap<>();
                queryObject.put(INSTITUTE_ID, instituteId);
                CampusEngagement campusEngagement = commonMongoRepository
                        .getEntityById(INSTITUTE_ID, instituteId, CampusEngagement.class);

                List<Article> articleList;
                if (Objects.nonNull(campusEngagement)) {
                    articleList = campusEngagement.getArticles();
                    if (Objects.isNull(articleList)) {
                        articleList = new ArrayList<>();
                    }
                } else {
                    articleList = new ArrayList<>();
                }

                Article article = new Article();
                article.setInstituteId(instituteId);
                article.setArticleShortDescription(xcelArticle.getArticleShortDescription());
                article.setArticleTitle(xcelArticle.getArticleTitle());
                article.setStudentPaytmMobileNumber(mobileNumber.trim());
                article.setSubmittedBy(xcelArticle.getSubmittedBy());
                article.setEmailAddress(xcelArticle.getEmailAddress());
                try {
                    article.setCreatedAt(campusEngagementHelper.convertDateFormat(XCEL_DATE_FORMAT,
                            DB_DATE_FORMAT, xcelArticle.getTimestamp()));
                    article.setSubmittedDate(
                            campusEngagementHelper.convertDateFormat(XCEL_SUBMITTED_DATE_FORMAT,
                                    DB_DATE_FORMAT, xcelArticle.getSubmittedDate()));
                } catch (Exception e) {
                    log.error("Error parsing date : {} ", e.getMessage());
                }

                if (StringUtils.isNotBlank(xcelArticle.getArticlePdf())) {
                    if (xcelArticle.getArticlePdf().startsWith(DRIVE_URL)) {
                        log.info("Going to upload image to s3");
                        if (setDocsFields(article, xcelArticle.getArticlePdf())) {
                            log.info("Image Upload successfully.");
                        } else {
                            log.info("error uploading image");
                        }
                    } else {
                        log.info("Image already uploaded. Setting the db path.");
                        xcelArticle.setArticlePdf(xcelArticle.getArticlePdf());
                    }
                }

                articleList.add(article);

                Map<String, Object> queryObject1 = new HashMap<>();
                queryObject1.put(INSTITUTE_ID, instituteId);
                List<String> fields = Arrays.asList(INSTITUTE_ID, ARTICLES);
                Update update = new Update();
                update.set(INSTITUTE_ID, instituteId);
                update.set(ARTICLES, articleList);
                commonMongoRepository.upsertData(queryObject1, fields, update,
                        CampusEngagement.class);

                campusAdminResponse.setEntity(String.valueOf(instituteId));
                campusAdminResponse.setMessage("Articles added successfully");
            } else {
                campusAdminResponse.setMessage("Article not added");
                campusAdminResponse.setError("Invalid Institute Id");
            }
        }
        return campusAdminResponse;
    }

    @Override public List<XcelArticle> getAllArticles() {
        List<XcelArticle> articleList = new ArrayList<>();
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put(EXISTS, true);
        queryMap.put(NE, null);

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put(ARTICLES, queryMap);

        List<CampusEngagement> campusEngagementList =
                mongoOperations.findAll(CampusEngagement.class);

        for (CampusEngagement campusEngagement : campusEngagementList) {
            if (Objects.nonNull(campusEngagement.getArticles())) {
                for (Article campusArticle : campusEngagement.getArticles()) {
                    XcelArticle xcelArticle = new XcelArticle();
                    BeanUtils.copyProperties(campusArticle, xcelArticle);
                    articleList.add(xcelArticle);
                }
            }
        }
        return articleList;
    }

    @Override public CampusAdminResponse addEvents(EventRequest xcelEvent) {
        CampusAdminResponse campusAdminResponse = new CampusAdminResponse();

        if (Objects.nonNull(xcelEvent)) {
            Long instituteId = xcelEvent.getInstituteId();
            if (Objects.nonNull(instituteId) && validInstitute(instituteId)) {

                Map<String, Object> queryObject = new HashMap<>();
                queryObject.put(INSTITUTE_ID, instituteId);
                CampusEngagement campusEngagement = commonMongoRepository
                        .getEntityById(INSTITUTE_ID, instituteId, CampusEngagement.class);

                List<CampusEvent> eventList = null;

                if (Objects.nonNull(campusEngagement)) {
                    eventList = campusEngagement.getEvents();
                    if (Objects.isNull(eventList)) {
                        eventList = new ArrayList<>();
                    }
                } else {
                    eventList = new ArrayList<>();
                }

                CampusEvent event = new CampusEvent();
                event.setInstituteId(instituteId);
                event.setEventTitle(xcelEvent.getEventTitle());
                event.setEventDescription(xcelEvent.getEventDescription());
                event.setEventType(xcelEvent.getEventType());
                event.setSubmittedBy(xcelEvent.getSubmittedBy());
                event.setEmailAddress(xcelEvent.getEmailAddress());
                try {
                    event.setCreatedAt(campusEngagementHelper.convertDateFormat(XCEL_DATE_FORMAT,
                            DB_DATE_FORMAT, xcelEvent.getTimestamp()));
                } catch (Exception e) {
                    log.error("Error parsing date : {}, expected format is : {}", e.getMessage(),
                            XCEL_DATE_FORMAT);
                }

                boolean isMediaEmpty = true;
                if (Objects.nonNull(xcelEvent.getEventMedia())) {
                    List<String> mediaUrl = Arrays.asList(xcelEvent.getEventMedia().split(","));
                    isMediaEmpty = setMediaFields(mediaUrl, instituteId, event);
                    if (isMediaEmpty) {
                        List<String> imageBuff = new ArrayList<>();
                        List<String> videoBuff = new ArrayList<>();
                        for (String media : mediaUrl) {
                            if (media.endsWith("jpg") || media.endsWith("jpeg") || media
                                    .endsWith("png")) {
                                imageBuff.add(media);
                            } else {
                                videoBuff.add(media);
                            }
                        }
                        if (!CollectionUtils.isEmpty(imageBuff)) {
                            event.setImages(imageBuff);
                        }
                        if (!CollectionUtils.isEmpty(videoBuff)) {
                            event.setVideos(videoBuff);
                        }
                    }
                }

                eventList.add(event);

                Map<String, Object> queryObject1 = new HashMap<>();
                queryObject1.put(INSTITUTE_ID, instituteId);
                List<String> fields = Arrays.asList(INSTITUTE_ID, EVENTS);
                Update update = new Update();
                update.set(INSTITUTE_ID, instituteId);
                update.set(EVENTS, eventList);
                commonMongoRepository.upsertData(queryObject1, fields, update,
                        CampusEngagement.class);

                campusAdminResponse.setEntity(String.valueOf(instituteId));
                campusAdminResponse.setMessage("Events added successfully");
            } else {
                campusAdminResponse.setMessage("Event not added");
                campusAdminResponse.setError("Invalid Institute Id");
            }
        }

        return campusAdminResponse;
    }

    @Override
    public List<XcelEvent> getAllEvents() {
        List<XcelEvent> eventList = new ArrayList<>();
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put(EXISTS, true);
        queryMap.put(NE, null);

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put(EVENTS, queryMap);

        List<CampusEngagement> campusEngagementList =
                mongoOperations.findAll(CampusEngagement.class);

        for (CampusEngagement campusEngagement : campusEngagementList) {
            if (Objects.nonNull(campusEngagement.getEvents())) {
                for (CampusEvent campusEvent : campusEngagement.getEvents()) {

                    List<String> images = campusEvent.getImages();
                    XcelEvent xcelEvent = new XcelEvent();
                    BeanUtils.copyProperties(campusEvent, xcelEvent);

                    StringBuffer mediaEvent = new StringBuffer();

                    if (Objects.nonNull(images)) {
                        for (String image : images) {
                            if (StringUtils.isNotBlank(image)) {
                                mediaEvent.append(image);
                            }
                        }
                    }

                    List<String> videos = campusEvent.getVideos();
                    if (Objects.nonNull(videos)) {
                        for (String video : videos) {
                            if (StringUtils.isNotBlank(video)) {
                                mediaEvent.append(mediaEvent);

                            }
                        }
                    }

                    xcelEvent.setEventMedia(mediaEvent.toString());
                    eventList.add(xcelEvent);

                }
            }
        }
        return eventList;
    }

    private boolean setMediaFields(CampusAmbassador ambassador, String mediaUrl) {
        String imageUrl = uploadUtil.uploadFile(mediaUrl, null, ambassador.getInstituteId(),
                S3_RELATIVE_PATH_FOR_AMBASSADOR, AwsConfig.getS3ExploreBucketName(),
                GoogleConfig.getCampusCredentialFileName(),
                GoogleConfig.getExploreCredentialFolderPath()).getKey();
        if (Objects.nonNull(imageUrl)) {
            log.info("Setting image URL : " + (DIRECTORY_SEPARATOR_SLASH + imageUrl));
            ambassador.setImageUrl(DIRECTORY_SEPARATOR_SLASH + imageUrl);
            return true;
        } else {
            return false;
        }
    }

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

    private boolean setMediaFields(List<String> mediaUrlList, long instituteId, CampusEvent event) {
        Map<String, List<String>> mediaMap = getMediaUrl(mediaUrlList, instituteId);
        boolean isMediaEmpty = true;
        if (Objects.nonNull(mediaMap)) {
            if (Objects.nonNull(mediaMap.get(IMAGE))) {
                isMediaEmpty = false;
                event.setImages(mediaMap.get(IMAGE));
            }
            if (Objects.nonNull(mediaMap.get(VIDEO))) {
                isMediaEmpty = false;
                event.setVideos(mediaMap.get(VIDEO));
            }
        }
        return isMediaEmpty;
    }

    private Map<String, List<String>> getMediaUrl(List<String> mediaUrlList, Long instituteId) {
        Map<String, List<String>> mediaMap = new HashMap<>();
        List<String> imageUrlList = new ArrayList<>();
        List<String> videoUrlList = new ArrayList<>();
        for (String url : mediaUrlList) {
            if (url.startsWith(DRIVE_URL)) {
                Pair<String, String> mediaInfo =
                        uploadUtil.uploadFile(url, null, instituteId, S3_RELATIVE_PATH_FOR_EVENT,
                                AwsConfig
                                        .getS3ExploreBucketName(),
                                GoogleConfig.getCampusCredentialFileName(),
                                GoogleConfig.getExploreCredentialFolderPath());
                if (Objects.nonNull(mediaInfo.getKey())) {
                    if (mediaInfo.getValue().startsWith(IMAGE)) {
                        imageUrlList.add(mediaInfo.getKey());
                    } else {
                        videoUrlList.add(mediaInfo.getKey());
                    }
                }
            }
        }
        if (!imageUrlList.isEmpty()) {
            mediaMap.put(IMAGE, imageUrlList);
        }
        if (!videoUrlList.isEmpty()) {
            mediaMap.put(VIDEO, videoUrlList);
        }
        return mediaMap;
    }

    private boolean validInstitute(Long instituteId) {
        Institute institute = commonMongoRepository
                .getEntityByFields(INSTITUTE_ID, instituteId, Institute.class,
                        Arrays.asList(INSTITUTE_ID));
        if (Objects.nonNull(institute)) {
            return true;
        }
        return false;
    }

    private boolean validMobileNumber(String mobileNumber) {
        if (Objects.nonNull(mobileNumber) && mobileNumber.trim().length() == 10) {
            if (mobileNumber.matches("[0-9]+")) {
                return true;
            }
        }
        return false;
    }
}
