package com.paytm.digital.education.explore.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.digital.education.config.AwsConfig;
import com.paytm.digital.education.config.GoogleConfig;
import com.paytm.digital.education.explore.database.entity.CampusAmbassador;
import com.paytm.digital.education.explore.database.entity.FailedCampusAmbassador;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.service.ImportDataService;
import com.paytm.digital.education.explore.service.helper.CampusEngagementHelper;
import com.paytm.digital.education.utility.GoogleDriveUtil;
import com.paytm.digital.education.explore.xcel.model.XcelCampusAmbassador;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.digital.education.utility.UploadUtil;
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

import static com.mongodb.QueryOperators.AND;
import static com.mongodb.QueryOperators.OR;
import static com.paytm.digital.education.explore.constants.AWSConstants.S3_RELATIVE_PATH_FOR_AMBASSADOR;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ATTRIBUTES;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.CAMPUS_AMBASSADORS;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.CAMPUS_AMBASSADOR_DATA_RANGE_TEMPLATE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.CAMPUS_AMBASSADOR_HEADER_RANGE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.CAMPUS_AMBASSADOR_SHEET_ID;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.CAMPUS_AMBASSADOR_START_ROW;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.DB_DATE_FORMAT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.FILE_DOWNLOAD_UPLOAD_FAILURE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.HAS_IMPORTED;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.INVALID_INSTITUTE_IDS;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.XCEL_DATE_FORMAT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;

@Slf4j
@Service
@AllArgsConstructor
public class ImportAmbassadorServiceImpl implements ImportDataService {

    private CommonMongoRepository  commonMongoRepository;
    private CampusEngagementHelper campusEngagementHelper;
    private UploadUtil             uploadUtil;

    /*
     ** Import the new data from spreadsheet
     */
    public boolean importData()
            throws IOException, GeneralSecurityException, ParseException {
        Map<String, Object> propertyMap = campusEngagementHelper.getCampusEngagementProperties();
        String sheetId = (String) propertyMap.get(CAMPUS_AMBASSADOR_SHEET_ID);
        String headerRange = (String) propertyMap.get(CAMPUS_AMBASSADOR_HEADER_RANGE);
        double startRow = (double) propertyMap.get(CAMPUS_AMBASSADOR_START_ROW);
        String dataRangeTemplate = (String) propertyMap.get(CAMPUS_AMBASSADOR_DATA_RANGE_TEMPLATE);
        List<Object> sheetAmbassadorData = GoogleDriveUtil.getDataFromSheet(sheetId,
                MessageFormat.format(dataRangeTemplate, startRow), headerRange,
                GoogleConfig.getCampusCredentialFileName(),
                GoogleConfig.getExploreCredentialFolderPath());
        if (Objects.nonNull(sheetAmbassadorData)) {
            int totalNumberOfData = sheetAmbassadorData.size();
            List<Object> failedDataList = new ArrayList<>();
            Map<Long, List<CampusAmbassador>> campusAmbassadorInstituteMap =
                    buildCampusAmbassadorInstituteMap(sheetAmbassadorData, failedDataList);
            int insertedCount = addCampusAmbassadors(campusAmbassadorInstituteMap, failedDataList);
            double updatedCount = startRow + totalNumberOfData;
            propertyMap.put(CAMPUS_AMBASSADOR_START_ROW, updatedCount);
            campusEngagementHelper.updatePropertyMap(ATTRIBUTES + "." + CAMPUS_AMBASSADOR_START_ROW,
                    updatedCount);
            if (totalNumberOfData != insertedCount) {
                log.info("Number of the failed campus ambassador data :"
                        + " {}", JsonUtils.toJson(totalNumberOfData - insertedCount));
                campusEngagementHelper.saveMultipleFailedData(failedDataList);
            }
        }
        return true;
    }

    /*
     ** Reimport the failed data
     */
    public boolean reimportFailedAmbassador() {
        Map<String, Object> searchRequest = new HashMap<>();
        searchRequest.put(HAS_IMPORTED, false);
        List<FailedCampusAmbassador> failedData = commonMongoRepository.findAll(searchRequest,
                FailedCampusAmbassador.class, new ArrayList<>(), AND);
        if (!failedData.isEmpty()) {
            List<Object> failedDataList = new ArrayList<>();
            Map<Long, List<CampusAmbassador>> campusAmbassadorInstituteMap =
                    buildAmbassadorInstituteMapFromFailedData(failedData, failedDataList);
            addCampusAmbassadors(campusAmbassadorInstituteMap, failedDataList);
            Update update = new Update();
            update.set(HAS_IMPORTED, true);
            Map<String, Object> queryObject = new HashMap<>();
            queryObject.put(HAS_IMPORTED, false);
            List<String> projectionFields = Arrays.asList(HAS_IMPORTED);
            commonMongoRepository.updateMulti(queryObject, projectionFields, update,
                    FailedCampusAmbassador.class);
            if (!failedDataList.isEmpty()) {
                campusEngagementHelper.saveMultipleFailedData(failedDataList);
            }
        }
        return true;
    }

    /*
     ** Insert or update the data in the db
     */
    private int addCampusAmbassadors(
            Map<Long, List<CampusAmbassador>> campusAmbassadorInstituteMap,
            List<Object> failedDataList) {
        Set<Long> instituteIdSet = campusAmbassadorInstituteMap.keySet();
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(INSTITUTE_ID, new ArrayList<>(instituteIdSet));
        List<String> instituteFields = Arrays.asList(INSTITUTE_ID, CAMPUS_AMBASSADORS);
        List<Institute> institutes = commonMongoRepository.findAll(queryObject, Institute.class,
                instituteFields, OR);
        Update update = new Update();
        int count = 0;
        for (Institute institute : institutes) {
            Map<String, CampusAmbassador> ambassadorMap = institute.getCampusAmbassadors();
            if (ambassadorMap == null) {
                ambassadorMap = new HashMap<>();
            }
            long instituteId = institute.getInstituteId();
            for (CampusAmbassador campusAmbassador :
                    campusAmbassadorInstituteMap.get(instituteId)) {
                String mobileNumber = campusAmbassador.getPaytmMobileNumber();
                CampusAmbassador existingAmbassadorData = ambassadorMap.get(mobileNumber);
                if (Objects.nonNull(existingAmbassadorData)) {
                    updateExistingData(existingAmbassadorData, campusAmbassador);
                }
                ambassadorMap.put(mobileNumber, campusAmbassador);
                count++;
            }
            update.set(CAMPUS_AMBASSADORS, ambassadorMap);
            queryObject.put(INSTITUTE_ID, instituteId);
            commonMongoRepository.updateFirst(queryObject, instituteFields, update,
                    Institute.class);
            instituteIdSet.remove(instituteId);
        }
        if (!instituteIdSet.isEmpty()) {
            List<Object> failedData =
                    campusAmbassadorInstituteMap.entrySet().stream()
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

    /*
     ** Update the existing data
     */
    private void updateExistingData(CampusAmbassador existingAmbassadorData,
            CampusAmbassador newAmbassadorData) {
        if (Objects.isNull(newAmbassadorData.getName())) {
            newAmbassadorData.setName(existingAmbassadorData.getName());
        }
        if (Objects.isNull(newAmbassadorData.getCourse())) {
            newAmbassadorData.setCourse(existingAmbassadorData.getCourse());
        }
        if (Objects.isNull(newAmbassadorData.getYearAndBatch())) {
            newAmbassadorData.setYearAndBatch(existingAmbassadorData.getYearAndBatch());
        }
        if (Objects.isNull(newAmbassadorData.getImageUrl())) {
            newAmbassadorData.setImageUrl(existingAmbassadorData.getImageUrl());
        }
        if (Objects.isNull(newAmbassadorData.getLastUpdated())) {
            newAmbassadorData.setLastUpdated(existingAmbassadorData.getLastUpdated());
        }
        if (Objects.isNull(newAmbassadorData.getEmailAddress())) {
            newAmbassadorData.setEmailAddress(existingAmbassadorData.getEmailAddress());
        }
    }

    /*
     ** Build ambassador institute map when input data is from spreadsheet
     */
    private Map<Long, List<CampusAmbassador>> buildCampusAmbassadorInstituteMap(
            List<Object> xcelAmbassadorData, List<Object> failedDataList) throws ParseException {
        Map<Long, List<CampusAmbassador>> campusAmbassadorsInstituteMap = new HashMap<>();
        for (Object object : xcelAmbassadorData) {
            ObjectMapper mapper = new ObjectMapper();
            XcelCampusAmbassador ambassador =
                    mapper.convertValue(object, XcelCampusAmbassador.class);
            CampusAmbassador campusAmbassador = new CampusAmbassador();
            campusAmbassador.setName(ambassador.getName());
            campusAmbassador.setCourse(ambassador.getCourse());
            Long instituteId = Long.parseLong(ambassador.getInstituteId());
            campusAmbassador.setInstituteId(instituteId);
            campusAmbassador.setPaytmMobileNumber(ambassador.getPaytmMobileNumber().trim());
            campusAmbassador.setYearAndBatch(ambassador.getYearAndBatch());
            campusAmbassador.setEmailAddress(ambassador.getEmailAddress());
            campusAmbassador.setCreatedAt(
                    campusEngagementHelper.convertDateFormat(XCEL_DATE_FORMAT,
                            DB_DATE_FORMAT, ambassador.getTimestamp()));
            campusAmbassador.setLastUpdated(campusAmbassador.getCreatedAt());
            if (ambassador.getImage() != null) {
                if (!setMediaFields(campusAmbassador, ambassador.getImage(), failedDataList)) {
                    continue;
                }
            }
            List<CampusAmbassador> instituteCampusAmbassasor =
                    campusAmbassadorsInstituteMap.get(campusAmbassador.getInstituteId());
            if (Objects.isNull(instituteCampusAmbassasor)) {
                instituteCampusAmbassasor = new ArrayList<>();
            }
            instituteCampusAmbassasor.add(campusAmbassador);
            campusAmbassadorsInstituteMap.put(instituteId, instituteCampusAmbassasor);
        }
        return campusAmbassadorsInstituteMap;
    }

    private FailedCampusAmbassador convertToFailedObject(CampusAmbassador a) {
        FailedCampusAmbassador b = new FailedCampusAmbassador();
        BeanUtils.copyProperties(a, b);
        b.setReason(INVALID_INSTITUTE_IDS);
        b.setTimestamp(a.getLastUpdated());
        b.setFailedDate(new Date());
        return b;
    }

    /*
     ** Build the ambassador institute Map when the input is failed ambassador data
     */
    private Map<Long, List<CampusAmbassador>> buildAmbassadorInstituteMapFromFailedData(
            List<FailedCampusAmbassador> failedCampusAmbassadors, List<Object> failedDataList) {
        Map<Long, List<CampusAmbassador>> responseAmbassador = new HashMap<>();
        for (FailedCampusAmbassador failedCampusAmbassador : failedCampusAmbassadors) {
            CampusAmbassador ambassador = new CampusAmbassador();
            BeanUtils.copyProperties(failedCampusAmbassador, ambassador);
            ambassador.setCreatedAt(failedCampusAmbassador.getTimestamp());
            if (Objects.nonNull(failedCampusAmbassador.getImageUrl())) {
                if (!setMediaFields(ambassador, failedCampusAmbassador.getImageUrl(),
                        failedDataList)) {
                    continue;
                }
            }
            List<CampusAmbassador> ambassadorList =
                    responseAmbassador.get(failedCampusAmbassador.getInstituteId());
            if (Objects.isNull(ambassadorList)) {
                ambassadorList = new ArrayList<>();
            }
            ambassadorList.add(ambassador);
            responseAmbassador.put(failedCampusAmbassador.getInstituteId(), ambassadorList);
        }
        return responseAmbassador;
    }

    /*
     ** Set Media if successfully upload and return true else return false
     */
    private boolean setMediaFields(CampusAmbassador ambassador, String mediaUrl,
            List<Object> failedDataList) {
        String imageUrl = uploadUtil.uploadFile(mediaUrl, null, ambassador.getInstituteId(),
                S3_RELATIVE_PATH_FOR_AMBASSADOR, AwsConfig.getS3ExploreBucketName(),
                GoogleConfig.getCampusCredentialFileName(),
                GoogleConfig.getExploreCredentialFolderPath()).getKey();
        if (Objects.nonNull(imageUrl)) {
            ambassador.setImageUrl(imageUrl);
            return true;
        } else {
            ambassador.setImageUrl(mediaUrl);
            FailedCampusAmbassador failedData = new FailedCampusAmbassador();
            BeanUtils.copyProperties(ambassador, failedData);
            failedData.setTimestamp(ambassador.getLastUpdated());
            failedData.setFailedDate(new Date());
            failedData.setReason(FILE_DOWNLOAD_UPLOAD_FAILURE);
            failedDataList.add(failedData);
            return false;
        }
    }
}
