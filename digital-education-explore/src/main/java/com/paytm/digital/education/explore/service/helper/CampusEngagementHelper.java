package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.database.entity.FailedData;
import com.paytm.digital.education.database.repository.FailedDataRepository;
import com.paytm.digital.education.database.entity.Article;
import com.paytm.digital.education.database.entity.CampusAmbassador;
import com.paytm.digital.education.explore.database.entity.CampusEngagement;
import com.paytm.digital.education.database.entity.CampusEvent;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.response.dto.detail.Ambassador;
import com.paytm.digital.education.explore.response.dto.detail.CampusArticle;
import com.paytm.digital.education.explore.response.dto.detail.CampusEventDetail;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.utility.DateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ATTRIBUTES;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.CAMPUS_ENGAGEMENT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.COMPONENT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.DOCS;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.GOOGLE_SHEETS_INFO;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.HAS_IMPORTED;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.KEY;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.MEDIA;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.NAMESPACE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.TYPE;
import static com.paytm.digital.education.constant.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_ID;

@Slf4j
@Service
@AllArgsConstructor
public class CampusEngagementHelper {
    private CommonMongoRepository commonMongoRepository;
    private PropertyReader        propertyReader;
    private FailedDataRepository  failedDataRepository;

    public void updatePropertyMap(String key, Object value) {
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(COMPONENT, EXPLORE_COMPONENT);
        queryObject.put(NAMESPACE, GOOGLE_SHEETS_INFO);
        queryObject.put(KEY, CAMPUS_ENGAGEMENT);
        List<String> fields = Arrays.asList(ATTRIBUTES);
        Update update = new Update();
        update.set(key, value);
        commonMongoRepository.updateFirst(queryObject, fields, update,
                Properties.class);
    }

    public Map<String, Object> getCampusEngagementProperties() {
        return propertyReader
                .getPropertiesAsMapByKey(EXPLORE_COMPONENT, GOOGLE_SHEETS_INFO, CAMPUS_ENGAGEMENT);
    }

    public List<Ambassador> getCampusAmbassadorData(
            Map<String, CampusAmbassador> campusAmbassadaorMap) {
        List<CampusAmbassador> campusAmbassadorList =
                campusAmbassadaorMap.values().stream().collect(Collectors.toList());
        List<Ambassador> responseAmbassadorList = new ArrayList<>();
        for (CampusAmbassador campusAmbassador : campusAmbassadorList) {
            Ambassador ambassador = new Ambassador();
            BeanUtils.copyProperties(campusAmbassador, ambassador);
            if (Objects.nonNull(campusAmbassador.getImageUrl())) {
                ambassador.setImageUrl(
                        CommonUtil.getAbsoluteUrl(campusAmbassador.getImageUrl(), MEDIA));
            }
            responseAmbassadorList.add(ambassador);
        }
        return responseAmbassadorList;
    }

    public List<CampusArticle> getCampusArticleData(List<Article> articles,
            Map<String, CampusAmbassador> campusAmbassadorMap) {
        List<CampusArticle> responseArticleList = new ArrayList<>();
        for (Article article : articles) {
            CampusArticle responseArticle = new CampusArticle();
            BeanUtils.copyProperties(article, responseArticle);
            if (Objects.nonNull(article.getArticlePdf())) {
                responseArticle
                        .setArticlePdf(CommonUtil.getAbsoluteUrl(article.getArticlePdf(),
                                DOCS));
                String phoneNumber = responseArticle.getStudentPaytmMobileNumber();
                if (Objects.nonNull(phoneNumber) && Objects.nonNull(campusAmbassadorMap)) {
                    CampusAmbassador ambassador = campusAmbassadorMap.get(phoneNumber.trim());
                    if (Objects.nonNull(ambassador)) {
                        responseArticle.setSubmittedBy(ambassador.getName());
                        responseArticle.setSubmitterImageUrl(
                                CommonUtil.getAbsoluteUrl(ambassador.getImageUrl(), MEDIA));
                        responseArticle.setSubmitterDesignation(ambassador.getYearAndBatch());
                    }
                }
            }
            responseArticleList.add(responseArticle);
        }
        return responseArticleList;
    }

    public List<CampusEventDetail> getCampusEventsData(List<CampusEvent> events) {
        List<CampusEventDetail> responseEventList = new ArrayList<>();
        for (CampusEvent event : events) {
            CampusEventDetail responseEvent = new CampusEventDetail();
            BeanUtils.copyProperties(event, responseEvent);
            if (Objects.nonNull(event.getImages())) {
                responseEvent.setImages(getAbsoluteUrlForAllTheMedia(event.getImages()));
            }
            if (Objects.nonNull(event.getVideos())) {
                responseEvent.setVideos(getAbsoluteUrlForAllTheMedia(event.getVideos()));
            }
            responseEventList.add(responseEvent);
        }
        return responseEventList;
    }

    private List<String> getAbsoluteUrlForAllTheMedia(List<String> mediaUrls) {
        List<String> mediaList = new ArrayList<>();
        for (String url : mediaUrls) {
            mediaList.add(CommonUtil.getAbsoluteUrl(url, MEDIA));
        }
        return mediaList;
    }

    public Date convertDateFormat(String currentPattern, String newPattern, String dateString)
            throws
            ParseException {
        DateFormat formatter = new SimpleDateFormat(currentPattern);
        Date date = formatter.parse(dateString);
        SimpleDateFormat newFormat = new SimpleDateFormat(newPattern);
        String finalString = newFormat.format(date);
        return newFormat.parse(finalString);
    }

    public void saveMultipleFailedData(List<Object> failedDataList) {
        if (!failedDataList.isEmpty()) {
            commonMongoRepository.saveMultipleObject(failedDataList);
        }
    }

    public void addToFailedList(Object object, String message,
            boolean isImportable, List<Object> failedDataList, String component, String type) {
        FailedData failedData = new FailedData();
        failedData.setComponent(component);
        failedData.setHasImported(false);
        failedData.setType(type);
        failedData.setMessage(message);
        failedData.setIsImportable(isImportable);
        failedData.setFailedDate(DateUtil.getCurrentDate());
        failedData.setData(object);
        failedDataList.add(failedData);
    }

    public void updateReimportStatus(String type, String component) {
        Update update = new Update();
        update.set(HAS_IMPORTED, true);
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(HAS_IMPORTED, false);
        queryObject.put(COMPONENT, component);
        queryObject.put(TYPE, type);
        List<String> projectionFields = Arrays.asList(HAS_IMPORTED);
        failedDataRepository.updateMulti(queryObject, projectionFields, update);
    }

    public CampusEngagement findCampusEngagementData(long instituteId) {
        List<CampusEngagement> campusEngagement =
                commonMongoRepository.getEntitiesByIdAndFields(INSTITUTE_ID,
                instituteId,
                CampusEngagement.class, new ArrayList<>());
        if (campusEngagement.isEmpty()) {
            return null;
        } else {
            return campusEngagement.get(0);
        }
    }
}
