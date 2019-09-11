package com.paytm.digital.education.coaching.ingestion.service;

import com.paytm.digital.education.coaching.http.HttpUtil;
import com.paytm.digital.education.coaching.http.RestTemplateFactory;
import com.paytm.digital.education.coaching.ingestion.model.IngestorResponse;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesResponse;
import com.paytm.digital.education.coaching.service.helper.IngestDataHelper;
import com.paytm.digital.education.config.GoogleConfig;
import com.paytm.digital.education.database.entity.FailedData;
import com.paytm.digital.education.database.repository.FailedDataRepository;
import com.paytm.digital.education.utility.DateUtil;
import com.paytm.digital.education.utility.GoogleDriveUtil;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
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
public abstract class IngestorServiceHelper {

    @Autowired
    public    IngestDataHelper     ingestDataHelper;
    @Autowired
    protected RestTemplateFactory  restTemplateFactory;
    @Autowired
    protected HttpUtil             httpUtil;
    @Autowired
    private   FailedDataRepository failedDataRepository;

    protected PropertiesResponse getProperties(final PropertiesRequest request) {
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
            final String sheetId = (String) propertyMap.get(request.getSheetIdKey());
            final String headerRange = (String) propertyMap.get(request.getSheetHeaderRangeKey());
            final double startRow = (double) propertyMap.get(request.getSheetStartRowKey());
            final String dataRangeTemplate = (String) propertyMap.get(
                    request.getSheetRangeTemplateKey());
            log.debug("Got: sheetId: {}, headerRange: {}, startRow: {}, dataRangeTemplate: {}",
                    sheetId, headerRange, startRow, dataRangeTemplate);

            return PropertiesResponse.builder()
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

    protected List<Object> getFormData(final PropertiesResponse propertiesResponse) {
        List<Object> formData = new ArrayList<>();
        if (null == propertiesResponse) {
            log.error("Got null propertiesResponse.");
            return formData;
        }
        try {
            formData = GoogleDriveUtil.getDataFromSheet(propertiesResponse.getSheetId(),
                    MessageFormat.format(propertiesResponse.getDataRangeTemplate(),
                            propertiesResponse.getStartRow()), propertiesResponse.getHeaderRange(),
                    GoogleConfig.getCoachingCredentialFileName(),
                    GoogleConfig.getCoachingCredentialFolderPath());
            log.debug("Got formData.size: {}", formData == null ? 0 : formData.size());
        } catch (final IOException | GeneralSecurityException e) {
            log.error("Got exception while getting data from google sheet, "
                    + "propertiesResponse: {}, exception: ", propertiesResponse, e);
        }
        return formData;
    }

    protected <T> IngestorResponse processRecords(final List<Object> formData,
            final List<T> failedFormList, final Class<T> clazz, final String type) {

        if (CollectionUtils.isEmpty(formData) && CollectionUtils.isEmpty(failedFormList)) {
            return IngestorResponse.builder().build();
        }

        this.processFailedRecords(failedFormList, clazz, type);

        final List<Object> failedDataList = new ArrayList<>();
        final List<T> newFormList = this.processNewRecords(formData, clazz, failedDataList);

        if (!failedDataList.isEmpty()) {
            this.failedDataRepository.saveAll(failedDataList);
        }

        return IngestorResponse.builder()
                .countOfNewRecordsProcessed(newFormList.size())
                .countOfFailedRecordsProcessed(failedDataList.size())
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
}
