package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.config.AwsConfig;
import com.paytm.digital.education.config.GoogleConfig;
import com.paytm.digital.education.database.entity.FailedData;
import com.paytm.digital.education.database.repository.FailedDataRepository;
import com.paytm.digital.education.explore.database.entity.CampusAmbassador;
import com.paytm.digital.education.explore.database.entity.CampusEngagement;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.service.ImportDataService;
import com.paytm.digital.education.explore.service.helper.CampusEngagementHelper;
import com.paytm.digital.education.utility.GoogleDriveUtil;
import com.paytm.digital.education.explore.xcel.model.XcelCampusAmbassador;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.digital.education.utility.UploadUtil;
import lombok.AllArgsConstructor;

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
import static com.paytm.digital.education.explore.constants.AWSConstants.S3_RELATIVE_PATH_FOR_AMBASSADOR;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ATTRIBUTES;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.CAMPUS_AMBASSADORS;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.CAMPUS_AMBASSADOR_DATA_RANGE_TEMPLATE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.CAMPUS_AMBASSADOR_HEADER_RANGE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.CAMPUS_AMBASSADOR_SHEET_ID;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.CAMPUS_AMBASSADOR_START_ROW;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.COMPONENT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.DB_DATE_FORMAT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.FILE_DOWNLOAD_UPLOAD_FAILURE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.HAS_IMPORTED;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.INVALID_INSTITUTE_IDS;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.IS_IMPORTABLE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.TYPE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.XCEL_DATE_FORMAT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;


@Service
@AllArgsConstructor
public class ImportAmbassadorServiceImpl implements ImportDataService {

    private CommonMongoRepository  commonMongoRepository;
    private CampusEngagementHelper campusEngagementHelper;
    private UploadUtil             uploadUtil;
    private FailedDataRepository   failedDataRepository;

    /*
     ** Import the new data from spreadsheet
     */
    public boolean importData(boolean isReimportOnly)
            throws IOException, GeneralSecurityException, ParseException {
        List<Object> sheetAmbassadorData = null;
        double startRow = 0;
        if (!isReimportOnly) {
            Map<String, Object> propertyMap =
                    campusEngagementHelper.getCampusEngagementProperties();
            String sheetId = (String) propertyMap.get(CAMPUS_AMBASSADOR_SHEET_ID);
            String headerRange = (String) propertyMap.get(CAMPUS_AMBASSADOR_HEADER_RANGE);
            startRow = (double) propertyMap.get(CAMPUS_AMBASSADOR_START_ROW);
            String dataRangeTemplate =
                    (String) propertyMap.get(CAMPUS_AMBASSADOR_DATA_RANGE_TEMPLATE);
            sheetAmbassadorData = GoogleDriveUtil.getDataFromSheet(sheetId,
                    MessageFormat.format(dataRangeTemplate, startRow), headerRange,
                    GoogleConfig.getCampusCredentialFileName(),
                    GoogleConfig.getExploreCredentialFolderPath());
        }
        List<Long> instituteIds = new ArrayList<>();
        List<XcelCampusAmbassador> xcelCampusAmbassadors = new ArrayList<>();
        List<Object> failedDataList = new ArrayList<>();
        List<String> failedPhoneNumber = new ArrayList<>();
        List<XcelCampusAmbassador> previousFailedAmbassadorList = getAllFailedData(instituteIds);
        if (Objects.nonNull(sheetAmbassadorData)) {
            xcelCampusAmbassadors = sheetAmbassadorData.stream()
                    .map(e2 -> JsonUtils.convertValue(e2, XcelCampusAmbassador.class))
                    .peek(xcelCampusAmbassador -> instituteIds
                            .add(xcelCampusAmbassador.getInstituteId()))
                    .collect(Collectors.toList());
        }
        Map<Long, Map<String, CampusAmbassador>> campusAmbassadorMap = new HashMap<>();
        List<Long> validInstituteIdList = new ArrayList<>();
        if (!instituteIds.isEmpty()) {
            Map<String, Object> queryObject = new HashMap<>();
            queryObject.put(INSTITUTE_ID, new ArrayList<>(instituteIds));
            List<CampusEngagement> campusEngagementList = commonMongoRepository.findAll(queryObject,
                    CampusEngagement.class,
                    Arrays.asList(CAMPUS_AMBASSADORS, INSTITUTE_ID), OR);
            campusAmbassadorMap = campusEngagementList.stream()
                .collect(HashMap::new,
                    (m, v) -> m.put(v.getInstituteId(), v.getCampusAmbassadors()),
                    HashMap::putAll);
            List<Institute> validInstitutes =
                    commonMongoRepository
                            .findAll(queryObject, Institute.class, Arrays.asList(INSTITUTE_ID), OR);
            validInstituteIdList = validInstitutes.stream().map(c -> c.getInstituteId())
                    .collect(Collectors.toList());
        }
        if (!previousFailedAmbassadorList.isEmpty()) {
            buildInstituteAmbassadorsMap(previousFailedAmbassadorList, validInstituteIdList,
                    campusAmbassadorMap, failedDataList, failedPhoneNumber);
            campusEngagementHelper.updateReimportStatus(CAMPUS_AMBASSADORS, EXPLORE_COMPONENT);
        }
        if (!xcelCampusAmbassadors.isEmpty()) {
            buildInstituteAmbassadorsMap(xcelCampusAmbassadors, validInstituteIdList,
                    campusAmbassadorMap, failedDataList, failedPhoneNumber);
        }
        if (!campusAmbassadorMap.isEmpty()) {
            saveCampusAmbassadors(campusAmbassadorMap);
        }
        if (Objects.nonNull(sheetAmbassadorData)) {
            campusEngagementHelper.updatePropertyMap(ATTRIBUTES + '.' + CAMPUS_AMBASSADOR_START_ROW,
                    startRow + sheetAmbassadorData.size());
        }
        if (!failedDataList.isEmpty()) {
            failedDataRepository.saveAll(failedDataList);
        }
        return true;
    }



    private void buildInstituteAmbassadorsMap(
            List<XcelCampusAmbassador> sheetDataList,
            List<Long> validInstituteIdList,
            Map<Long, Map<String, CampusAmbassador>> campusEngagementMap,
            List<Object> failedDataList, List<String> failedPhoneNumbers)
            throws ParseException {
        for (XcelCampusAmbassador ambassador : sheetDataList) {
            Long instituteId = ambassador.getInstituteId();
            if (Objects.nonNull(instituteId) && validInstituteIdList.contains(instituteId)) {
                String mobileNumber = ambassador.getPaytmMobileNumber();
                if (StringUtils.isNotBlank(mobileNumber)) {
                    mobileNumber = mobileNumber.trim();
                    String uniqueMobileNoString = instituteId + '-' + mobileNumber;
                    if (!failedPhoneNumbers.contains(uniqueMobileNoString)) {
                        Map<String, CampusAmbassador> existingCampusAmbassador =
                                campusEngagementMap.get(instituteId);
                        if (Objects.isNull(existingCampusAmbassador)) {
                            existingCampusAmbassador = new HashMap<>();
                        }
                        CampusAmbassador campusAmbassador =
                                existingCampusAmbassador.get(mobileNumber);
                        if (Objects.isNull(campusAmbassador)) {
                            campusAmbassador = new CampusAmbassador();
                        }
                        if (StringUtils.isNotBlank(ambassador.getName())) {
                            campusAmbassador.setName(ambassador.getName());
                        }
                        if (StringUtils.isNotBlank(ambassador.getCourse())) {
                            campusAmbassador.setCourse(ambassador.getCourse());
                        }
                        campusAmbassador.setInstituteId(instituteId);
                        if (StringUtils.isNotBlank(mobileNumber)) {
                            campusAmbassador.setPaytmMobileNumber(mobileNumber);
                        }
                        if (StringUtils.isNotBlank(ambassador.getYearAndBatch())) {
                            campusAmbassador.setYearAndBatch(ambassador.getYearAndBatch());
                        }
                        if (StringUtils.isNotBlank(ambassador.getEmailAddress())) {
                            campusAmbassador.setEmailAddress(ambassador.getEmailAddress());
                        }
                        campusAmbassador.setCreatedAt(
                                campusEngagementHelper.convertDateFormat(XCEL_DATE_FORMAT,
                                        DB_DATE_FORMAT, ambassador.getTimestamp()));
                        campusAmbassador.setLastUpdated(campusAmbassador.getCreatedAt());
                        if (StringUtils.isNotBlank(ambassador.getImage())) {
                            if (!setMediaFields(campusAmbassador, ambassador.getImage())) {
                                // Failure case handling S3 upload failed
                                campusEngagementHelper.addToFailedList(ambassador,
                                        FILE_DOWNLOAD_UPLOAD_FAILURE, true,
                                        failedDataList,
                                        EXPLORE_COMPONENT, CAMPUS_AMBASSADORS);
                                failedPhoneNumbers.add(uniqueMobileNoString);
                                continue;
                            }
                        }
                        existingCampusAmbassador.put(mobileNumber, campusAmbassador);
                        campusEngagementMap.put(instituteId, existingCampusAmbassador);
                    } else {
                        campusEngagementHelper
                                .addToFailedList(ambassador, "The data respective to this phone "
                                                + "number is failed before.", true,
                                        failedDataList,
                                        EXPLORE_COMPONENT, CAMPUS_AMBASSADORS);
                    }
                } else {
                    campusEngagementHelper
                            .addToFailedList(ambassador, "Phone number is required.", false,
                                    failedDataList,
                                    EXPLORE_COMPONENT, CAMPUS_AMBASSADORS);
                }
            } else {
                // Failure case handling invalid institute Id
                campusEngagementHelper
                        .addToFailedList(ambassador, INVALID_INSTITUTE_IDS, false, failedDataList,
                                EXPLORE_COMPONENT, CAMPUS_AMBASSADORS);
            }
        }
    }

    /*
     ** Set Media if successfully upload and return true else return false
     */
    private boolean setMediaFields(CampusAmbassador ambassador, String mediaUrl) {
        String imageUrl = uploadUtil.uploadFile(mediaUrl, null, ambassador.getInstituteId(),
                S3_RELATIVE_PATH_FOR_AMBASSADOR, AwsConfig.getS3ExploreBucketName(),
                GoogleConfig.getCampusCredentialFileName(),
                GoogleConfig.getExploreCredentialFolderPath()).getKey();
        if (Objects.nonNull(imageUrl)) {
            ambassador.setImageUrl(imageUrl);
            return true;
        } else {
            return false;
        }
    }

    private List<XcelCampusAmbassador> getAllFailedData(List<Long> instituteIds) {
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(COMPONENT, EXPLORE_COMPONENT);
        queryObject.put(TYPE, CAMPUS_AMBASSADORS);
        queryObject.put(HAS_IMPORTED, false);
        queryObject.put(IS_IMPORTABLE, true);
        List<FailedData> failedCampusAmbasssadorList = failedDataRepository.findAll(queryObject);
        List<XcelCampusAmbassador> failedData =
                failedCampusAmbasssadorList.stream().map(c -> JsonUtils.convertValue(c.getData(),
                        XcelCampusAmbassador.class)).peek(campusAmbassador -> instituteIds
                        .add(campusAmbassador.getInstituteId())).collect(Collectors.toList());
        return failedData;
    }

    private void saveCampusAmbassadors(
            Map<Long, Map<String, CampusAmbassador>> campusAmbassadorMap) {
        for (Map.Entry<Long, Map<String, CampusAmbassador>> entry : campusAmbassadorMap
                .entrySet()) {
            if (Objects.nonNull(entry.getValue())) {
                Map<String, Object> queryObject = new HashMap<>();
                queryObject.put(INSTITUTE_ID, entry.getKey());
                List<String> fields = Arrays.asList(INSTITUTE_ID, CAMPUS_AMBASSADORS);
                Update update = new Update();
                update.set(INSTITUTE_ID, entry.getKey());
                update.set(CAMPUS_AMBASSADORS, entry.getValue());
                commonMongoRepository.upsertData(queryObject, fields, update,
                        CampusEngagement.class);
            }
        }
    }
}
