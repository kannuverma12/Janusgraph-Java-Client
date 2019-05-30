package com.paytm.digital.education.explore.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.digital.education.explore.database.entity.CampusEvent;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.service.ImportDataService;
import com.paytm.digital.education.explore.service.helper.CampusEngagementHelper;
import com.paytm.digital.education.explore.utility.CommonUtil;
import com.paytm.digital.education.explore.utility.GoogleDriveUtil;
import com.paytm.digital.education.explore.xcel.model.XcelEvent;
import javafx.util.Pair;
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
import static com.paytm.digital.education.explore.constants.AWSConstants.S3_PATH_FOR_EVENT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ATTRIBUTES;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.EVENTS;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.EVENT_DATA_RANGE_TEMPLATE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.EVENT_HEADER_RANGE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.EVENT_SHEET_ID;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.EVENT_START_ROW;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.IMAGE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.VIDEO;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;

@Slf4j
@Service
@AllArgsConstructor
public class ImportEventServiceImpl implements ImportDataService {
    private CommonMongoRepository  commonMongoRepository;
    private CampusEngagementHelper campusEngagementHelper;

    public Map<Long, List<CampusEvent>> importData()
            throws IOException, GeneralSecurityException {
        Map<String, Object> propertyMap = campusEngagementHelper.getCampusEngagementProperties();
        String sheetId = (String) propertyMap.get(EVENT_SHEET_ID);
        String headerRange = (String) propertyMap.get(EVENT_HEADER_RANGE);
        double startRow = (double) propertyMap.get(EVENT_START_ROW);
        String dataRangeTemplate = (String) propertyMap.get(EVENT_DATA_RANGE_TEMPLATE);
        List<Object> eventData = GoogleDriveUtil.getDataFromSheet(sheetId,
                MessageFormat.format(dataRangeTemplate, startRow), headerRange);
        if (Objects.nonNull(eventData)) {
            Map<Long, List<CampusEvent>> articleInstituteMap =
                    buildEventInstituteMap(eventData);
            int insertedCount = addEvents(articleInstituteMap);
            if (insertedCount > 0) {
                double updatedCount = startRow + insertedCount;
                propertyMap.put(EVENT_START_ROW, updatedCount);
                campusEngagementHelper
                        .updatePropertyMap(ATTRIBUTES + "." + EVENT_START_ROW, updatedCount);
            }
        }
        return null;
    }

    /*
     ** Events related methods
     */
    private Map<Long, List<CampusEvent>> buildEventInstituteMap(
            List<Object> xcelEvents) throws IOException,
            GeneralSecurityException {
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
            event.setCreatedAt(xcelEvent.getTimestamp());
            if (xcelEvent.getEventMedia() != null) {
                Map<String, List<String>> mediaMap = getMediaUrl(xcelEvent.getEventMedia(),
                        instituteId);
                if (!mediaMap.isEmpty()) {
                    if (Objects.nonNull(mediaMap.get(IMAGE))) {
                        event.setImages(mediaMap.get(IMAGE));
                    }
                    if (Objects.nonNull(mediaMap.get(VIDEO))) {
                        event.setVideos(mediaMap.get(VIDEO));
                    }
                }
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
        for (String url : googleMediaUrl) {
            Pair<String, String> mediaInfo = CommonUtil.uploadToS3(url, null, instituteId,
                    S3_BUCKET_PATH,
                    S3_PATH_FOR_EVENT);
            if (mediaInfo.getValue().startsWith(IMAGE)) {
                imageUrlList.add(mediaInfo.getKey());
            } else {
                videoUrlList.add(mediaInfo.getKey());
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

    private int addEvents(
            Map<Long, List<CampusEvent>> eventInstituteMap) {
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
            for (CampusEvent event :
                    eventInstituteMap.get(institute.getInstituteId())) {
                eventList.add(event);
                count++;
            }
            update.set(EVENTS, eventList);
            queryObject.put(INSTITUTE_ID, institute.getInstituteId());
            commonMongoRepository.updateFirst(queryObject, instituteFields, update,
                    Institute.class);
        }
        return count;
    }
}
