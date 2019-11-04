package com.paytm.digital.education.coaching.ingestion.service.importdata;

import com.paytm.digital.education.coaching.exeption.CoachingBaseException;
import com.paytm.digital.education.coaching.ingestion.model.ImportResponse;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.IngestDataHelper;
import com.paytm.digital.education.coaching.ingestion.service.IngestionHelper;
import com.paytm.digital.education.config.GoogleConfig;
import com.paytm.digital.education.database.entity.FailedData;
import com.paytm.digital.education.database.repository.FailedDataRepository;
import com.paytm.digital.education.utility.DateUtil;
import com.paytm.digital.education.utility.GoogleDriveUtil;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.digital.education.utility.UploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.FailedDataCollection.COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.FailedDataCollection.HAS_IMPORTED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.FailedDataCollection.IS_IMPORTABLE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.FailedDataCollection.TYPE;

@Slf4j
@Component
public abstract class AbstractImportService {

    private static final int    PADDING              = 2;
    private static final String SHEET_HEADER_RANGE   = "A1:";
    private static final String SHEET_RANGE_TEMPLATE = "A2:";

    @Value("${ingestion.env.profile}")
    protected String envProfile;
    @Value("${coaching.s3.bucketName}")
    protected String coachingS3BucketName;
    @Value("${coaching.s3.path}")
    protected String coachingS3Path;

    @Autowired
    protected IngestDataHelper     ingestDataHelper;
    @Autowired
    protected UploadUtil           uploadUtil;
    @Autowired
    private   FailedDataRepository failedDataRepository;

    protected DataImportPropertiesResponse getProperties(final Class clazz,
            final DataImportPropertiesRequest request) {
        if (null == request) {
            log.error("Got null properties request.");
            return null;
        }

        final Map<String, Object> propertyMap = this.ingestDataHelper.getDataIngestionProperties();
        if (CollectionUtils.isEmpty(propertyMap)) {
            log.error("Got null/ empty propertyMap.");
            return null;
        }

        try {
            final List<Object> headersList = IngestionHelper.getHeaderKeysList(clazz);
            final String range = IngestionHelper.convertNumberToA1Notation(headersList.size(),
                    PADDING);

            final String sheetId = (String) propertyMap.get(request.getSheetIdKey());
            final String headerRange = SHEET_HEADER_RANGE + range + "1";
            final double startRow = 2;
            final String dataRangeTemplate = SHEET_RANGE_TEMPLATE + range;

            log.debug("Got: sheetId: {}, headerRange: {}, startRow: {}, dataRangeTemplate: {}",
                    sheetId, headerRange, startRow, dataRangeTemplate);

            return DataImportPropertiesResponse.builder()
                    .sheetId(sheetId)
                    .headerRange(headerRange)
                    .startRow(startRow)
                    .dataRangeTemplate(dataRangeTemplate)
                    .build();
        } catch (final NullPointerException e) {
            log.error("Got exception for propertiesRequest: {}, exception: ", request, e);
            return null;
        }
    }

    protected List<Object> getFormData(
            final DataImportPropertiesResponse dataImportPropertiesResponse) {
        List<Object> formData = new ArrayList<>();
        if (null == dataImportPropertiesResponse) {
            log.error("Got null dataImportPropertiesResponse.");
            return formData;
        }
        try {
            formData = GoogleDriveUtil.getDataFromSheet(dataImportPropertiesResponse.getSheetId(),
                    MessageFormat.format(dataImportPropertiesResponse.getDataRangeTemplate(),
                            dataImportPropertiesResponse.getStartRow()),
                    dataImportPropertiesResponse
                            .getHeaderRange(),
                    GoogleConfig.getCoachingCredentialFileName(),
                    GoogleConfig.getCoachingCredentialFolderPath());
            log.debug("Got formData.size: {}", formData == null ? 0 : formData.size());
        } catch (final IOException | GeneralSecurityException e) {
            log.error("Got exception while getting data from google sheet, "
                            + "dataImportPropertiesResponse: {}, exception: ",
                    dataImportPropertiesResponse, e);
        }
        return formData;
    }

    protected <T> ImportResponse processRecords(final List<Object> formData,
            final List<T> failedFormList, final Class<T> clazz, final String type) {

        if (CollectionUtils.isEmpty(formData) && CollectionUtils.isEmpty(failedFormList)) {
            return ImportResponse.builder().build();
        }

        this.processFailedRecords(failedFormList, clazz, type);

        final List<Object> failedDataList = new ArrayList<>();
        final List<T> newFormList = this.processNewRecords(formData, clazz, failedDataList);

        if (!failedDataList.isEmpty()) {
            this.failedDataRepository.saveAll(failedDataList);
        }

        return ImportResponse.builder()
                .countOfNewRecordsProcessed(newFormList.size())
                .countOfFailedRecordsProcessed(failedFormList == null ? 0 : failedFormList.size())
                .countOfNewRecordsFailed(failedDataList.size())
                .build();
    }

    private <T> void processFailedRecords(List<T> failedFormList, Class<T> clazz, String type) {
        if (!CollectionUtils.isEmpty(failedFormList)) {
            for (final T failedForm : failedFormList) {
                this.upsertFailedRecords(failedForm, clazz);
            }
            this.updateReImportStatus(type, COACHING);
        }
    }

    private <T> List<T> processNewRecords(List<Object> formData, Class<T> clazz,
            List<Object> failedDataList) {
        final List<T> newFormList = this.convertObjectToClass(formData, clazz);
        if (!CollectionUtils.isEmpty(newFormList)) {
            for (final T form : newFormList) {
                this.upsertNewRecords(form, failedDataList, clazz);
            }
        }
        return newFormList;
    }

    protected abstract <T> void upsertFailedRecords(final T form, final Class<T> clazz);

    protected abstract <T> void upsertNewRecords(final T form, final List<Object> failedDataList,
            final Class<T> clazz);

    protected <T> List<T> convertObjectToClass(final List<Object> formData, final Class<T> clazz) {
        if (CollectionUtils.isEmpty(formData) || null == clazz) {
            log.error("Got null/ empty formData or null clazz, formData: {}, clazz: {}",
                    formData, clazz);
            return new ArrayList<>();
        }
        final List<T> formList = formData.stream()
                .filter(Objects::nonNull)
                .map(o -> JsonUtils.convertValue(o, clazz))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.info("Input- formData: {}, clazz: {} and output- formList: {}",
                formData, clazz, formList);

        return formList;
    }

    protected <T> List<T> getFailedData(final String type, final Class<T> clazz,
            final String component) {
        final Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(COMPONENT, component);
        queryObject.put(TYPE, type);
        queryObject.put(HAS_IMPORTED, false);
        queryObject.put(IS_IMPORTABLE, true);

        final List<FailedData> failedDataList = failedDataRepository.findAll(queryObject);

        return failedDataList.stream()
                .map(failedData -> JsonUtils.fromJson((String) failedData.getData(), clazz))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    protected void addToFailedList(final Object object, final String message,
            final boolean isImportable, final String component, final String type,
            final List<Object> failedDataList) {

        final FailedData failedData = new FailedData();
        failedData.setComponent(component);
        failedData.setHasImported(false);
        failedData.setType(type);
        failedData.setMessage(message);
        failedData.setIsImportable(isImportable);
        failedData.setFailedDate(DateUtil.getCurrentDate());
        failedData.setData(object);

        failedDataList.add(failedData);
    }

    private void updateReImportStatus(final String type, final String component) {
        final Update update = new Update();
        update.set(HAS_IMPORTED, true);

        final Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(COMPONENT, component);
        queryObject.put(TYPE, type);
        queryObject.put(HAS_IMPORTED, false);

        this.failedDataRepository.updateMulti(queryObject,
                Collections.singletonList(HAS_IMPORTED), update);
    }

    protected String uploadFile(final String driveImageUrl, final String imagePrefix)
            throws CoachingBaseException {

        if (StringUtils.isEmpty(driveImageUrl) || StringUtils.isEmpty(imagePrefix)) {
            log.error("Got empty upload file request, driveImageUrl: {}, imagePrefix: {}",
                    driveImageUrl, imagePrefix);
            return null;
        }

        final Pair<String, String> fileNameAndMemeTypePair = this.uploadUtil
                .downloadFileFromGoogleDriveAndUploadToS3(driveImageUrl,
                        String.valueOf(Instant.now().toEpochMilli()), null,
                        this.envProfile + imagePrefix,
                        this.coachingS3BucketName + this.coachingS3Path,
                        GoogleConfig.getCoachingCredentialFileName(),
                        GoogleConfig.getCoachingCredentialFolderPath());
        log.debug("fileNameAndMemeTypePair: {}", fileNameAndMemeTypePair.toString());

        if (null != fileNameAndMemeTypePair.getKey()) {
            log.debug("File uploaded in s3 for driveImageUrl: {}, imagePrefix: {}, filePath: {}",
                    driveImageUrl, imagePrefix, fileNameAndMemeTypePair.getKey());

            final String s3Path = fileNameAndMemeTypePair.getKey();
            String filePath = "";
            if (s3Path.contains(this.envProfile + imagePrefix)) {
                filePath = s3Path.replace(this.envProfile + imagePrefix, "");
            }
            return filePath;
        } else {
            throw new CoachingBaseException(String.format("Failed to upload file in s3, "
                    + "driveImageUrl: %s, imagePrefix: %s", driveImageUrl, imagePrefix));
        }
    }
}
