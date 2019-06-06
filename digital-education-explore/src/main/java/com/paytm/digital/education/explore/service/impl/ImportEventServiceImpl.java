package com.paytm.digital.education.explore.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.digital.education.explore.database.entity.CampusEvent;
import com.paytm.digital.education.explore.database.entity.FailedEvent;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.service.ImportDataService;
import com.paytm.digital.education.explore.service.helper.CampusEngagementHelper;
import com.paytm.digital.education.explore.utility.GoogleDriveUtil;
import com.paytm.digital.education.explore.xcel.model.XcelEvent;
import com.paytm.digital.education.utility.JsonUtils;
import javafx.util.Pair;
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
import static com.paytm.digital.education.explore.constants.AWSConstants.S3_RELATIVE_PATH_FOR_EVENT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ATTRIBUTES;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.DB_DATE_FORMAT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.EVENTS;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.EVENT_DATA_RANGE_TEMPLATE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.EVENT_HEADER_RANGE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.EVENT_SHEET_ID;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.EVENT_START_ROW;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.FAILED_MEDIA;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.FILE_DOWNLOAD_UPLOAD_FAILURE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.IMAGE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.INVALID_INSTITUTE_IDS;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.VIDEO;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.XCEL_DATE_FORMAT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;

@Slf4j
@Service
@AllArgsConstructor
public class ImportEventServiceImpl implements ImportDataService {
    private CommonMongoRepository  commonMongoRepository;
    private CampusEngagementHelper campusEngagementHelper;

    public boolean importData()
            throws IOException, GeneralSecurityException, ParseException {
        Map<String, Object> propertyMap = campusEngagementHelper.getCampusEngagementProperties();
        String sheetId = (String) propertyMap.get(EVENT_SHEET_ID);
        String headerRange = (String) propertyMap.get(EVENT_HEADER_RANGE);
        double startRow = (double) propertyMap.get(EVENT_START_ROW);
        String dataRangeTemplate = (String) propertyMap.get(EVENT_DATA_RANGE_TEMPLATE);
        List<Object> eventData = GoogleDriveUtil.getDataFromSheet(sheetId,
                MessageFormat.format(dataRangeTemplate, startRow), headerRange);
        if (Objects.nonNull(eventData)) {
            int totalNumberOfData = eventData.size();
            List<Object> failedDataList = new ArrayList<>();
            Map<Long, List<CampusEvent>> articleInstituteMap =
                    buildEventInstituteMap(eventData, failedDataList);
            int insertedCount = addEvents(articleInstituteMap, failedDataList);
            double updatedCount = startRow + totalNumberOfData;
            propertyMap.put(EVENT_START_ROW, updatedCount);
            campusEngagementHelper
                    .updatePropertyMap(ATTRIBUTES + "." + EVENT_START_ROW, updatedCount);
            if (totalNumberOfData != insertedCount) {
                log.info("Number of the failed article data :"
                        + " {}", JsonUtils.toJson(totalNumberOfData - insertedCount));
                campusEngagementHelper.saveMultipleFailedData(failedDataList);
            }

        }
        return true;
    }

    /*
     ** Events related methods
     */
    private Map<Long, List<CampusEvent>> buildEventInstituteMap(
            List<Object> xcelEvents, List<Object> failedDataList) throws IOException,
            GeneralSecurityException, ParseException {
        Map<Long, List<CampusEvent>> eventInstituteMap = new HashMap<>();
        for (Object object : xcelEvents) {
            ObjectMapper mapper = new ObjectMapper();
            XcelEvent xcelEvent = mapper.convertValue(object, XcelEvent.class);
            CampusEvent event = new CampusEvent();
            Long instituteId = Long.parseLong(xcelEvent.getInstituteId());
            event.setInstituteId(instituteId);
            event.setEventTitle(xcelEvent.getEventTitle());
            event.setEventDescription(xcelEvent.getEventDescription());
            event.setEventType(xcelEvent.getEventType());
            event.setSubmittedBy(xcelEvent.getSubmittedBy());
            event.setCreatedAt(campusEngagementHelper.convertDateFormat(XCEL_DATE_FORMAT,
                    DB_DATE_FORMAT, xcelEvent.getTimestamp()));
            boolean isMediaEmpty = true;
            if (xcelEvent.getEventMedia() != null) {
                Map<String, List<String>> mediaMap = getMediaUrl(xcelEvent.getEventMedia(),
                        instituteId);
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
                        FailedEvent failedEvent = new FailedEvent();
                        BeanUtils.copyProperties(event, failedEvent);
                        failedEvent.setReason(FILE_DOWNLOAD_UPLOAD_FAILURE);
                        failedEvent.setTimestamp(event.getCreatedAt());
                        failedEvent.setFailedDate(new Date());
                        failedDataList.add(failedEvent);
                    }
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
        }
        return eventInstituteMap;
    }

    private Map<String, List<String>> getMediaUrl(String mediaUrls, Long instituteId)
            throws GeneralSecurityException, IOException {
        String[] googleMediaUrl = mediaUrls.split(",");
        Map<String, List<String>> mediaMap = new HashMap<>();
        List<String> imageUrlList = new ArrayList<>();
        List<String> videoUrlList = new ArrayList<>();
        List<String> failedUrlList = new ArrayList<>();
        for (String url : googleMediaUrl) {
            Pair<String, String> mediaInfo =
                    campusEngagementHelper.uploadFile(url, null, instituteId, S3_RELATIVE_PATH_FOR_EVENT);
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

    private int addEvents(
            Map<Long, List<CampusEvent>> eventInstituteMap, List<Object> failedDataList) {
        Set<Long> instituteIdSet = eventInstituteMap.keySet();
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(INSTITUTE_ID, new ArrayList<>(instituteIdSet));
        List<String> instituteFields = Arrays.asList(INSTITUTE_ID, EVENTS);
        List<Institute> institutes = commonMongoRepository.findAll(queryObject, Institute.class,
                instituteFields, OR);
        Update update = new Update();
        int count = 0;
        for (Institute institute : institutes) {
            List<CampusEvent> eventList = institute.getEvents();
            if (eventList == null) {
                eventList = new ArrayList<>();
            }
            long instituteId = institute.getInstituteId();
            for (CampusEvent event :
                    eventInstituteMap.get(instituteId)) {
                eventList.add(event);
                count++;
            }
            update.set(EVENTS, eventList);
            queryObject.put(INSTITUTE_ID, instituteId);
            commonMongoRepository.updateFirst(queryObject, instituteFields, update,
                    Institute.class);
            instituteIdSet.remove(instituteId);
        }
        if (!instituteIdSet.isEmpty()) {
            log.info("Invalid Institute Ids while importing events data : {}",
                    JsonUtils.toJson(instituteIdSet));
            List<Object> failedData = eventInstituteMap.entrySet().stream()
                .filter(x -> instituteIdSet.contains(x.getKey()))
                .flatMap(e1 -> e1.getValue().stream()
                        .map(e2 -> convertToFailedObject(e2)))
                .collect(Collectors.toList());
            if (!failedData.isEmpty()) {
                failedDataList.addAll(failedData);
            }
        }
        return count;
    }

    private FailedEvent convertToFailedObject(CampusEvent a) {
        FailedEvent b = new FailedEvent();
        BeanUtils.copyProperties(a, b);
        b.setReason(INVALID_INSTITUTE_IDS);
        b.setTimestamp(a.getCreatedAt());
        b.setFailedDate(new Date());
        return b;
    }
}
