package com.paytm.digital.education.coaching.service.impl;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.database.entity.CoachingInstitute;
import com.paytm.digital.education.coaching.database.entity.KeyHighlight;
import com.paytm.digital.education.coaching.database.entity.OfficialAddress;
import com.paytm.digital.education.coaching.database.repository.CoachingInstituteRepository;
import com.paytm.digital.education.coaching.enums.CourseType;
import com.paytm.digital.education.coaching.googlesheet.model.CoachingInstituteForm;
import com.paytm.digital.education.coaching.service.helper.IngestDataHelper;
import com.paytm.digital.education.config.AwsConfig;
import com.paytm.digital.education.config.GoogleConfig;
import com.paytm.digital.education.database.entity.FailedData;
import com.paytm.digital.education.database.repository.FailedDataRepository;
import com.paytm.digital.education.utility.DateUtil;
import com.paytm.digital.education.utility.GoogleDriveUtil;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.digital.education.utility.UploadUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.ACTIVE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.HAS_IMPORTED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.HTTPS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_SHEET_HEADER_RANGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_SHEET_RANGE_TEMPLATE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_SHEET_START_ROW;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.IS_IMPORTABLE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.S3_UPLOAD_FAILED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TYPE;

@Service
@AllArgsConstructor
public class ImportInstituteService {
    private IngestDataHelper            ingestDataHelper;
    private CoachingInstituteRepository coachingInstituteRepository;
    private FailedDataRepository        failedDataRepository;
    private UploadUtil                  uploadUtil;

    /*
     ** Import the new data from spreadsheet
     */
    public boolean importData()
            throws IOException, GeneralSecurityException {
        Map<String, Object> propertyMap = ingestDataHelper.getDataIngestionProperties();
        String sheetId = (String) propertyMap.get(INSTITUTE_SHEET_ID);
        String headerRange = (String) propertyMap.get(INSTITUTE_SHEET_HEADER_RANGE);
        double startRow = (double) propertyMap.get(INSTITUTE_SHEET_START_ROW);
        String dataRangeTemplate = (String) propertyMap.get(INSTITUTE_SHEET_RANGE_TEMPLATE);
        List<Object> instituteSheetData = GoogleDriveUtil.getDataFromSheet(sheetId,
                MessageFormat.format(dataRangeTemplate, startRow), headerRange,
                GoogleConfig.getCoachingCredentialFileName(),
                GoogleConfig.getCoachingCredentialFolderPath());
        List<Long> instituteIds = new ArrayList<>();
        List<CoachingInstituteForm> coachingInstituteFormSheetData = new ArrayList<>();
        Map<Long, List<FailedData>> failedDataMap = new HashMap<>();
        List<CoachingInstitute> previousFailedInstituteList = getAllFailedData(instituteIds);
        if (Objects.nonNull(instituteSheetData)) {
            coachingInstituteFormSheetData = instituteSheetData.stream()
                    .map(e2 -> JsonUtils.convertValue(e2, CoachingInstituteForm.class))
                    .peek(coachingInstituteForm -> instituteIds
                            .add(coachingInstituteForm.getInstituteId()))
                    .collect(Collectors.toList());
        }
        Map<Long, CoachingInstitute> instituteMap = new HashMap<>();
        if (!instituteIds.isEmpty()) {
            List<CoachingInstitute> existingInstitutes =
                    coachingInstituteRepository.findAllCoachingInstitutes(instituteIds);
            instituteMap = existingInstitutes.stream()
                    .collect(Collectors.toMap(c -> c.getInstituteId(), c -> c));
        }
        Set<Long> failedInstituteIds = new HashSet<>();
        if (!previousFailedInstituteList.isEmpty()) {
            reimportFailedInstituteData(previousFailedInstituteList, instituteMap, failedDataMap,
                    failedInstituteIds);
            ingestDataHelper.updateReimportStatus(INSTITUTE, COACHING);
        }
        if (!coachingInstituteFormSheetData.isEmpty()) {
            addCoachingInstituteData(coachingInstituteFormSheetData, instituteMap, failedDataMap,
                    failedInstituteIds);
        }
        if (!failedDataMap.isEmpty()) {
            List<Object> failedDataList = failedDataMap.values().stream()
                    .collect(ArrayList::new, List::addAll, List::addAll);
            failedDataRepository.saveAll(failedDataList);
        }
        //Update the next read row no. of excel in property map
        ingestDataHelper.updatePropertyMap(INSTITUTE_SHEET_START_ROW, instituteSheetData, startRow);
        return true;
    }

    private List<CoachingInstitute> getAllFailedData(List<Long> instituteIds) {
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(COMPONENT, COACHING);
        queryObject.put(TYPE, INSTITUTE);
        queryObject.put(HAS_IMPORTED, false);
        queryObject.put(IS_IMPORTABLE, true);
        List<FailedData> failedInstituteList = failedDataRepository.findAll(queryObject);
        List<CoachingInstitute> coachingInstituteList =
                failedInstituteList.stream().map(c -> JsonUtils.convertValue(c.getData(),
                        CoachingInstitute.class)).peek(coachingInstitute -> instituteIds
                        .add(coachingInstitute.getInstituteId())).collect(Collectors.toList());
        return coachingInstituteList;
    }

    private void addCoachingInstituteData(
            List<CoachingInstituteForm> instituteSheetDataList,
            Map<Long, CoachingInstitute> instituteMap,
            Map<Long, List<FailedData>> failedDataMap, Set<Long> failedInstituteIds) {
        boolean isFailed;
        boolean isImportable;
        String message = null;
        for (CoachingInstituteForm sheetData : instituteSheetDataList) {
            CoachingInstitute coachingInstitute = new CoachingInstitute();
            Long instituteId = sheetData.getInstituteId();
            isFailed = false;
            isImportable = true;
            if (Objects.nonNull(instituteId)) {
                CoachingInstitute existingDetails = instituteMap.get(instituteId);
                if (Objects.nonNull(existingDetails) && !failedInstituteIds.contains(instituteId)) {
                    BeanUtils.copyProperties(existingDetails, coachingInstitute);
                } else {
                    coachingInstitute.setInstituteId(instituteId);
                    List<FailedData> previousFailedData = failedDataMap.get(instituteId);
                    if (Objects.nonNull(previousFailedData)) {
                        FailedData failedData = previousFailedData.get(0);
                        isFailed = false;
                        message = failedData.getMessage();
                        isImportable = failedData.getIsImportable();
                        coachingInstitute =
                                JsonUtils.convertValue(failedData.getData(),
                                        CoachingInstitute.class);
                    } else {
                        isFailed = true;
                        message = "Invalid Institute Id";
                        isImportable = false;
                    }
                }
            }
            String instituteName = sheetData.getInstituteName();
            if (StringUtils.isNotBlank(instituteName)) {
                coachingInstitute.setInstituteName(instituteName);
            }
            String brandName = sheetData.getBrandName();
            if (StringUtils.isNotBlank(brandName)) {
                coachingInstitute.setBrandName(brandName);
            }
            String aboutInstitute = sheetData.getAboutInstitute();
            if (StringUtils.isNotBlank(aboutInstitute)) {
                coachingInstitute.setAboutInstitute(aboutInstitute);
            }
            String streamsPrepared = sheetData.getStreamPrepared();
            if (StringUtils.isNotBlank(streamsPrepared)) {
                List<String> streams = Arrays.asList(streamsPrepared.split(", "));
                coachingInstitute.setStreamsPreparedFor(streams);
            }
            String examPreparedIds = sheetData.getExamPreparedIds();
            if (StringUtils.isNotBlank(examPreparedIds)) {
                List<String> examIdsInString = Arrays.asList(examPreparedIds.split(", "));
                List<Long> examIds = examIdsInString.stream().map(e2 -> Long.parseLong(e2)).collect(
                        Collectors.toList());
                coachingInstitute.setExamsPreparedFor(examIds);
            }
            String coursesAvailable = sheetData.getCoursesAvailable();
            if (StringUtils.isNotBlank(coursesAvailable)) {
                List<String> courses = Arrays.asList(coursesAvailable.split(", "));
                List<CourseType> courseTypeList = new ArrayList<>();
                for (String course : courses) {
                    CourseType courseType = CourseType.fromString(course);
                    if (Objects.nonNull(courseType)) {
                        courseTypeList.add(courseType);
                    }
                }
                coachingInstitute.setCoursesTypeAvailable(courseTypeList);
            }
            Integer yearOfEstablishment = sheetData.getYearOfEstablishment();
            if (Objects.nonNull(yearOfEstablishment)) {
                coachingInstitute.setEstablishmentYear(yearOfEstablishment);
            }
            String stepsToApply = sheetData.getStepsToApply();
            if (StringUtils.isNotBlank(stepsToApply)) {
                coachingInstitute.setStepsToApply(stepsToApply);
            }
            String logo = sheetData.getLogo();
            if (StringUtils.isNotBlank(logo)) {
                coachingInstitute.setLogo(logo);
            }
            String coverImage = sheetData.getCoverImage();
            if (StringUtils.isNotBlank(coverImage)) {
                coachingInstitute.setCoverImage(coverImage);
            }
            String scholarshipMatrix = sheetData.getScholarshipMatrix();
            if (StringUtils.isNotBlank(scholarshipMatrix)) {
                coachingInstitute.setScholarshipMatrix(scholarshipMatrix);
            }
            String brochure = sheetData.getBrochure();
            if (StringUtils.isNotBlank(brochure)) {
                coachingInstitute.setBrochure(brochure);
            }
            String status = sheetData.getStatus().toLowerCase();
            if (StringUtils.isNotBlank(status)) {
                if (status.equals(ACTIVE)) {
                    coachingInstitute.setActive(true);
                } else {
                    coachingInstitute.setActive(false);
                }
            }
            OfficialAddress officialAddress = coachingInstitute.getOfficialAddress();
            if (Objects.isNull(officialAddress)) {
                officialAddress = new OfficialAddress();
            }
            String addressLine1 = sheetData.getAddressLine1();
            if (StringUtils.isNotBlank(addressLine1)) {
                officialAddress.setAddressLine1(addressLine1);
            }
            String addressLine2 = sheetData.getAddressLine2();
            if (StringUtils.isNotBlank(addressLine2)) {
                officialAddress.setAddressLine2(addressLine2);
            }
            String city = sheetData.getCity();
            if (StringUtils.isNotBlank(city)) {
                officialAddress.setCity(city);
            }
            String state = sheetData.getState();
            if (StringUtils.isNotBlank(state)) {
                officialAddress.setState(state);
            }
            String pincode = sheetData.getPincode();
            if (StringUtils.isNotBlank(pincode)) {
                officialAddress.setPincode(pincode);
            }
            String phoneNumber = sheetData.getPrimaryPhoneNumber();
            if (StringUtils.isNotBlank(phoneNumber)) {
                officialAddress.setPhone(phoneNumber);
            }
            String emailAddress = sheetData.getPrimaryEmailId();
            if (StringUtils.isNotBlank(emailAddress)) {
                officialAddress.setEmail(emailAddress);
            }
            String latitude = sheetData.getLatitude();
            if (StringUtils.isNotBlank(latitude)) {
                officialAddress.setLatitude(latitude);
            }
            String longitude = sheetData.getLongitude();
            if (StringUtils.isNotBlank(longitude)) {
                officialAddress.setLongitude(longitude);
            }
            String officialName = sheetData.getOfficialName();
            if (StringUtils.isNotBlank(officialName)) {
                officialAddress.setName(officialName);
            }
            if (Objects.nonNull(officialAddress.getAddressLine1())) {
                coachingInstitute.setOfficialAddress(officialAddress);
            }
            updateKeyHighlights(sheetData, coachingInstitute);
            if (Objects.nonNull(instituteId) && !isFailed) {
                isFailed = uploadFiles(coachingInstitute);
                if (isFailed) {
                    message = S3_UPLOAD_FAILED;
                }
            }
            if (!isFailed && isImportable) {
                CoachingInstitute dbCoachingData =
                        coachingInstituteRepository.upsertCoaching(coachingInstitute);
                if (Objects.nonNull(instituteId)) {
                    instituteMap.put(instituteId, coachingInstitute);
                    failedDataMap.remove(instituteId);
                } else {
                    coachingInstitute.setInstituteId(dbCoachingData.getInstituteId());
                    isFailed = uploadFiles(coachingInstitute);
                    if (isFailed) {
                        addToFailedList(coachingInstitute, S3_UPLOAD_FAILED, failedDataMap,
                                failedInstituteIds,
                                isImportable);
                    } else {
                        coachingInstituteRepository.upsertCoaching(coachingInstitute);
                    }
                }
            } else {
                addToFailedList(coachingInstitute, message, failedDataMap, failedInstituteIds,
                        isImportable);
            }
        }
    }

    private void addToFailedList(CoachingInstitute coachingInstitute, String message,
            Map<Long, List<FailedData>> failedDataMap, Set<Long> failedInstituteIds,
            boolean isImportable) {
        FailedData failedData = new FailedData();
        failedData.setComponent(COACHING);
        failedData.setHasImported(false);
        failedData.setType(INSTITUTE);
        failedData.setMessage(message);
        failedData.setIsImportable(isImportable);
        failedData.setFailedDate(DateUtil.getCurrentDate());
        failedData.setData(coachingInstitute);
        Long instituteId = coachingInstitute.getInstituteId();
        if (Objects.nonNull(instituteId)) {
            failedInstituteIds.add(instituteId);
            failedDataMap.put(instituteId, Arrays.asList(failedData));
        } else {
            List<FailedData> failedNewData = failedDataMap.get((long) 0);
            if (Objects.isNull(failedNewData)) {
                failedNewData = new ArrayList<>();
            }
            failedNewData.add(failedData);
            failedDataMap.put((long) 0, failedNewData);
        }
    }

    private boolean reimportFailedInstituteData(List<CoachingInstitute> coachingInstituteList,
            Map<Long, CoachingInstitute> instituteMap, Map<Long, List<FailedData>> failedDataMap,
            Set<Long> failedInstituteIds) {
        boolean isFailed;
        String message = null;
        for (CoachingInstitute coachingInstitute : coachingInstituteList) {
            isFailed = uploadFiles(coachingInstitute);
            if (!isFailed) {
                coachingInstituteRepository.upsertCoaching(coachingInstitute);
                if (Objects.nonNull(coachingInstitute.getInstituteId())) {
                    instituteMap.put(coachingInstitute.getInstituteId(), coachingInstitute);
                    failedDataMap.remove(coachingInstitute.getInstituteId());
                }
            } else {
                addToFailedList(coachingInstitute, message, failedDataMap, failedInstituteIds,
                        true);
            }
        }
        return true;
    }

    private void updateKeyHighlights(CoachingInstituteForm sheetData,
            CoachingInstitute dbData) {
        Map<Integer, KeyHighlight> keyHighlightMap = dbData.getKeyhighlights();
        if (Objects.isNull(keyHighlightMap)) {
            keyHighlightMap = new HashMap<>();
        }
        updateKeyHighlightMap(sheetData.getHighlight1AttributeName(),
                sheetData.getHighlight1Value(), 1, keyHighlightMap);
        updateKeyHighlightMap(sheetData.getHighlight2AttributeName(),
                sheetData.getHighlight2Value(), 2, keyHighlightMap);
        updateKeyHighlightMap(sheetData.getHighlight3AttributeName(),
                sheetData.getHighlight3Value(), 3, keyHighlightMap);
        updateKeyHighlightMap(sheetData.getHighlight4AttributeName(),
                sheetData.getHighlight4Value(), 4, keyHighlightMap);
        updateKeyHighlightMap(sheetData.getHighlight5AttributeName(),
                sheetData.getHighlight5Value(), 5, keyHighlightMap);
        updateKeyHighlightMap(sheetData.getHighlight6AttributeName(),
                sheetData.getHighlight6Value(), 6, keyHighlightMap);
        updateKeyHighlightMap(sheetData.getHighlight7AttributeName(),
                sheetData.getHighlight7Value(), 7, keyHighlightMap);
        updateKeyHighlightMap(sheetData.getHighlight8AttributeName(),
                sheetData.getHighlight8Value(), 8, keyHighlightMap);
        updateKeyHighlightMap(sheetData.getHighlight9AttributeName(),
                sheetData.getHighlight9Value(), 9, keyHighlightMap);
        updateKeyHighlightMap(sheetData.getHighlight10AttributeName(),
                sheetData.getHighlight10Value(), 10, keyHighlightMap);
        dbData.setKeyhighlights(keyHighlightMap);
    }

    private void updateKeyHighlightMap(String attributeName, String attributeValue,
            Integer position, Map<Integer, KeyHighlight> keyHighlightMap) {
        if (StringUtils.isNotBlank(attributeName) && StringUtils
                .isNotBlank(attributeValue)) {
            KeyHighlight keyHighlight = new KeyHighlight();
            keyHighlight.setAttributeName(attributeName);
            keyHighlight.setAttributeValue(attributeValue);
            keyHighlightMap.put(position, keyHighlight);
        }
    }

    private boolean uploadFiles(CoachingInstitute coachingInstitute) {
        Long instituteId = coachingInstitute.getInstituteId();
        boolean isFailed = false;
        String logo = coachingInstitute.getLogo();
        if (StringUtils.isNotBlank(logo) && logo.startsWith(HTTPS)) {
            String relativeUrl = uploadUtil.uploadFile(logo, null,
                    instituteId, CoachingConstants.S3RelativePath.LOGO,
                    AwsConfig.getS3CoachingBucketName(),
                    GoogleConfig.getCoachingCredentialFileName(),
                    GoogleConfig.getCoachingCredentialFolderPath()).getKey();
            if (StringUtils.isNotBlank(relativeUrl)) {
                coachingInstitute.setLogo(relativeUrl);
            } else {
                isFailed = true;
            }
        }
        String coverImage = coachingInstitute.getCoverImage(); // s3 upload
        if (StringUtils.isNotBlank(coverImage) && coverImage.startsWith(HTTPS)) {
            String relativeUrl = uploadUtil.uploadFile(coverImage, null,
                    instituteId, CoachingConstants.S3RelativePath.COVER_IMAGE,
                    AwsConfig.getS3CoachingBucketName(),
                    GoogleConfig.getCoachingCredentialFileName(),
                    GoogleConfig.getCoachingCredentialFolderPath()).getKey();
            if (StringUtils.isNotBlank(relativeUrl)) {
                coachingInstitute.setCoverImage(relativeUrl);
            } else {
                isFailed = true;
            }
        }
        String scholarshipMatrix = coachingInstitute.getScholarshipMatrix();
        if (StringUtils.isNotBlank(scholarshipMatrix) && scholarshipMatrix.startsWith(HTTPS)) {  //
            // S3 upload
            String relativeUrl = uploadUtil.uploadFile(logo, null,
                    instituteId, CoachingConstants.S3RelativePath.SCHOLARMATRIX,
                    AwsConfig.getS3CoachingBucketName(),
                    GoogleConfig.getCoachingCredentialFileName(),
                    GoogleConfig.getCoachingCredentialFolderPath()).getKey();
            if (StringUtils.isNotBlank(relativeUrl)) {
                coachingInstitute.setScholarshipMatrix(relativeUrl);
            } else {
                isFailed = true;
            }
        }
        String brochure = coachingInstitute.getBrochure();
        if (StringUtils.isNotBlank(brochure) && brochure.startsWith(HTTPS)) {  //
            // S3 upload
            String relativeUrl = uploadUtil.uploadFile(brochure, null,
                    instituteId, CoachingConstants.S3RelativePath.BROCHURE,
                    AwsConfig.getS3CoachingBucketName(),
                    GoogleConfig.getCoachingCredentialFileName(),
                    GoogleConfig.getCoachingCredentialFolderPath()).getKey();
            if (StringUtils.isNotBlank(relativeUrl)) {
                coachingInstitute.setScholarshipMatrix(relativeUrl);
            } else {
                isFailed = true;
            }
        }
        return isFailed;
    }
}
