package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.config.AwsConfig;
import com.paytm.digital.education.config.GoogleConfig;
import com.paytm.digital.education.database.entity.FailedData;
import com.paytm.digital.education.database.repository.FailedDataRepository;
import com.paytm.digital.education.explore.database.entity.CampusEngagement;
import com.paytm.digital.education.explore.database.entity.CampusEvent;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.service.ImportDataService;
import com.paytm.digital.education.explore.service.helper.CampusEngagementHelper;
import com.paytm.digital.education.utility.GoogleDriveUtil;
import com.paytm.digital.education.explore.xcel.model.XcelEvent;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.digital.education.utility.UploadUtil;
import javafx.util.Pair;
import lombok.AllArgsConstructor;

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
import static com.paytm.digital.education.explore.constants.AWSConstants.S3_RELATIVE_PATH_FOR_EVENT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ATTRIBUTES;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.COMPONENT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.DB_DATE_FORMAT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.EVENTS;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.EVENT_DATA_RANGE_TEMPLATE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.EVENT_HEADER_RANGE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.EVENT_SHEET_ID;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.EVENT_START_ROW;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.FAILED_MEDIA;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.FILE_DOWNLOAD_UPLOAD_FAILURE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.HAS_IMPORTED;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.IMAGE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.INVALID_INSTITUTE_IDS;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.IS_IMPORTABLE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.TYPE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.VIDEO;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.XCEL_DATE_FORMAT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;


@Service
@AllArgsConstructor
public class ImportEventServiceImpl implements ImportDataService {
    private CommonMongoRepository  commonMongoRepository;
    private CampusEngagementHelper campusEngagementHelper;
    private UploadUtil             uploadUtil;
    private FailedDataRepository   failedDataRepository;

    /*
     ** Method to import new event data
     */
    public boolean importData(boolean isReimportOnly)
            throws IOException, GeneralSecurityException, ParseException {
        List<Object> eventSheetData = null;
        double startRow = 0;
        if (!isReimportOnly) {
            Map<String, Object> propertyMap =
                    campusEngagementHelper.getCampusEngagementProperties();
            String sheetId = (String) propertyMap.get(EVENT_SHEET_ID);
            String headerRange = (String) propertyMap.get(EVENT_HEADER_RANGE);
            startRow = (double) propertyMap.get(EVENT_START_ROW);
            String dataRangeTemplate = (String) propertyMap.get(EVENT_DATA_RANGE_TEMPLATE);
            eventSheetData = GoogleDriveUtil.getDataFromSheet(sheetId,
                    MessageFormat.format(dataRangeTemplate, startRow), headerRange,
                    GoogleConfig.getCampusCredentialFileName(),
                    GoogleConfig.getExploreCredentialFolderPath());
        }
        List<XcelEvent> xcelEvents = new ArrayList<>();
        List<Long> instituteIds = new ArrayList<>();
        List<Object> failedDataList = new ArrayList<>();
        List<XcelEvent> previousFailedEventsList = getAllFailedData(instituteIds);
        if (Objects.nonNull(eventSheetData)) {
            xcelEvents = eventSheetData.stream()
                    .map(e2 -> JsonUtils.convertValue(e2, XcelEvent.class))
                    .peek(xcelEvent -> instituteIds
                            .add(xcelEvent.getInstituteId()))
                    .collect(Collectors.toList());
        }
        List<Long> validInstituteIdList = new ArrayList<>();
        Map<Long, List<CampusEvent>> eventMap = new HashMap<>();
        if (!instituteIds.isEmpty()) {
            Map<String, Object> queryObject = new HashMap<>();
            queryObject.put(INSTITUTE_ID, new ArrayList<>(instituteIds));
            List<CampusEngagement> campusEngagementList = commonMongoRepository.findAll(queryObject,
                    CampusEngagement.class,
                    Arrays.asList(EVENTS, INSTITUTE_ID), OR);
            if (Objects.nonNull(campusEngagementList)) {
                eventMap = campusEngagementList.stream()
                        .collect(HashMap::new, (m, v) -> m.put(v.getInstituteId(), v.getEvents()),
                                HashMap::putAll);
            }
            List<Institute> validInstitutes =
                    commonMongoRepository
                            .findAll(queryObject, Institute.class, Arrays.asList(INSTITUTE_ID), OR);
            validInstituteIdList = validInstitutes.stream().map(c -> c.getInstituteId())
                    .collect(Collectors.toList());
        }
        if (!previousFailedEventsList.isEmpty()) {
            buildEventInstituteMap(previousFailedEventsList, validInstituteIdList,
                    eventMap, failedDataList);
            campusEngagementHelper.updateReimportStatus(EVENTS, EXPLORE_COMPONENT);
        }
        if (!xcelEvents.isEmpty()) {
            buildEventInstituteMap(xcelEvents, validInstituteIdList,
                    eventMap, failedDataList);
        }
        if (!eventMap.isEmpty()) {
            saveEvents(eventMap);
        }
        if (Objects.nonNull(eventSheetData)) {
            campusEngagementHelper.updatePropertyMap(ATTRIBUTES + '.' + EVENT_START_ROW,
                    startRow + eventSheetData.size());
        }
        if (!failedDataList.isEmpty()) {
            failedDataRepository.saveAll(failedDataList);
        }
        return true;
    }



    /*
     ** Build the event institute Map when input data is from the spreadSheet
     */
    private Map<Long, List<CampusEvent>> buildEventInstituteMap(
            List<XcelEvent> xcelEvents, List<Long> validInstituteIdList, Map<Long,
            List<CampusEvent>> eventInstituteMap, List<Object> failedDataList)
            throws ParseException {
        for (XcelEvent xcelEvent : xcelEvents) {
            Long instituteId = xcelEvent.getInstituteId();
            if (Objects.nonNull(instituteId) && validInstituteIdList.contains(instituteId)) {
                CampusEvent event = new CampusEvent();
                event.setInstituteId(instituteId);
                event.setEventTitle(xcelEvent.getEventTitle());
                event.setEventDescription(xcelEvent.getEventDescription());
                event.setEventType(xcelEvent.getEventType());
                event.setSubmittedBy(xcelEvent.getSubmittedBy());
                event.setEmailAddress(xcelEvent.getEmailAddress());
                event.setCreatedAt(campusEngagementHelper.convertDateFormat(XCEL_DATE_FORMAT,
                        DB_DATE_FORMAT, xcelEvent.getTimestamp()));
                boolean isMediaEmpty = true;
                if (Objects.nonNull(xcelEvent.getEventMedia())) {
                    List<String> mediaUrl = Arrays.asList(xcelEvent.getEventMedia().split(","));
                    isMediaEmpty = setMediaFields(mediaUrl, instituteId, event);
                    if (Objects.nonNull(event.getFailedMedia())) {
                        if (!isMediaEmpty) {
                            xcelEvent.setEventMedia(String.join(",", event.getFailedMedia()));
                        }
                        campusEngagementHelper
                                .addToFailedList(xcelEvent, FILE_DOWNLOAD_UPLOAD_FAILURE, true,
                                        failedDataList, EXPLORE_COMPONENT, EVENTS);
                    }
                }
                if (isMediaEmpty) {
                    continue;
                }
                List<CampusEvent> instituteEvent = eventInstituteMap.get(instituteId);
                if (Objects.isNull(instituteEvent)) {
                    instituteEvent = new ArrayList<>();
                }
                instituteEvent.add(event);
                eventInstituteMap.put(instituteId, instituteEvent);
            } else {
                // Failure case handling
                campusEngagementHelper
                        .addToFailedList(xcelEvent, INVALID_INSTITUTE_IDS, false,
                                failedDataList, EXPLORE_COMPONENT, EVENTS);

            }
        }
        return eventInstituteMap;
    }

    /*
     ** Set the media fields of the campus event model and return response if all the media
     * uploaded failed(true) else false
     */
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
            if (Objects.nonNull(FAILED_MEDIA)) {
                event.setFailedMedia(mediaMap.get(FAILED_MEDIA));
            }
        }
        return isMediaEmpty;
    }

    /*
     ** Get the media url after the file is uploaded to S3
     */
    private Map<String, List<String>> getMediaUrl(List<String> mediaUrlList, Long instituteId) {
        Map<String, List<String>> mediaMap = new HashMap<>();
        List<String> imageUrlList = new ArrayList<>();
        List<String> videoUrlList = new ArrayList<>();
        List<String> failedUrlList = new ArrayList<>();
        for (String url : mediaUrlList) {
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
            } else {
                failedUrlList.add(url);
            }
        }
        if (!imageUrlList.isEmpty()) {
            mediaMap.put(IMAGE, imageUrlList);
        }
        if (!videoUrlList.isEmpty()) {
            mediaMap.put(VIDEO, videoUrlList);
        }
        if (!failedUrlList.isEmpty()) {
            mediaMap.put(FAILED_MEDIA, failedUrlList);
        }
        return mediaMap;
    }

    private void saveEvents(Map<Long, List<CampusEvent>> eventInstituteMap) {
        for (Map.Entry<Long, List<CampusEvent>> entry : eventInstituteMap
                .entrySet()) {
            if (Objects.nonNull(entry.getValue())) {
                Map<String, Object> queryObject = new HashMap<>();
                queryObject.put(INSTITUTE_ID, entry.getKey());
                List<String> fields = Arrays.asList(INSTITUTE_ID, EVENTS);
                Update update = new Update();
                update.set(INSTITUTE_ID, entry.getKey());
                update.set(EVENTS, entry.getValue());
                commonMongoRepository.upsertData(queryObject, fields, update,
                        CampusEngagement.class);
            }
        }
    }

    private List<XcelEvent> getAllFailedData(List<Long> instituteIds) {
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(COMPONENT, EXPLORE_COMPONENT);
        queryObject.put(TYPE, EVENTS);
        queryObject.put(HAS_IMPORTED, false);
        queryObject.put(IS_IMPORTABLE, true);
        List<FailedData> failedEventList = failedDataRepository.findAll(queryObject);
        List<XcelEvent> failedData =
                failedEventList.stream().map(c -> JsonUtils.convertValue(c.getData(),
                        XcelEvent.class)).peek(event -> instituteIds
                        .add(event.getInstituteId())).collect(Collectors.toList());
        return failedData;
    }
}
