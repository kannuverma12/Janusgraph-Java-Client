package com.paytm.digital.education.coaching.ingestion.service.exportdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataExportPropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataExportPropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.IngestionHelper;
import com.paytm.digital.education.config.GoogleConfig;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.utility.GoogleDriveUtil;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DATA_EXPORT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.GOOGLE_SHEETS_INFO;

@Component
public abstract class AbstractExportService {

    private static final Logger log = LoggerFactory.getLogger(AbstractExportService.class);

    @Autowired
    private PropertyReader propertyReader;

    protected DataExportPropertiesResponse getProperties(DataExportPropertiesRequest request) {
        if (null == request) {
            log.error("Got null exportData properties request.");
            return null;
        }
        final Map<String, Object> propertyMap = this.getDataExportProperties();
        if (CollectionUtils.isEmpty(propertyMap)) {
            log.error("Got null/ empty propertyMap.");
            return null;
        }
        try {
            final String sheetId = (String) propertyMap.get(request.getSheetIdKey());
            log.debug("Got: sheetId: {}", sheetId);
            return DataExportPropertiesResponse.builder().sheetId(sheetId).build();
        } catch (final NullPointerException e) {
            log.error("Got exception for exportData, propertiesRequest: {}", e, request);
            return null;
        }
    }

    protected <T> int processRecords(final List<T> list, final Class clazz,
            final String sheetId) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }

        final List<List<Object>> values = this.convertListToObjectList(list, clazz);
        if (CollectionUtils.isEmpty(values)) {
            return 0;
        }

        final String range =
                "A1:" + IngestionHelper.convertNumberToA1Notation(values.get(0).size());
        log.debug("Clazz: {}, Range: {}", clazz, range);

        try {
            UpdateValuesResponse updateValuesResponse = GoogleDriveUtil.writeDataToSheet(
                    sheetId, range, values, GoogleConfig.getCoachingCredentialFileName(),
                    GoogleConfig.getCoachingCredentialFolderPath());
            return updateValuesResponse.getUpdatedRows();
        } catch (final IOException | GeneralSecurityException e) {
            log.error("Got exception in processRecords, list: {}, clazz:{}, sheetId: {}, "
                    + "range: {}", e, list, clazz, sheetId, range);
            return 0;
        }
    }

    private Map<String, Object> getDataExportProperties() {
        return this.propertyReader.getPropertiesAsMapByKey(COACHING_COMPONENT,
                GOOGLE_SHEETS_INFO, DATA_EXPORT);
    }

    private <T> List<List<Object>> convertListToObjectList(
            final List<T> inputList, final Class clazz) {

        final List<List<Object>> values = new ArrayList<>();
        if (CollectionUtils.isEmpty(inputList) || null == clazz) {
            log.error("Got Empty inputList or null clazz, inputList: {}, clazz: {}",
                    inputList, clazz);
            return values;
        }

        final List<Object> headerKeysList = IngestionHelper.getHeaderKeysList(clazz);
        if (CollectionUtils.isEmpty(headerKeysList)) {
            log.error("Got empty headers, inputList: {}, clazz: {}", inputList, clazz);
            return values;
        }
        values.add(headerKeysList);

        final List<Object> headersList = this.getHeadersList(clazz);

        List<Object> valuesSubList;
        for (final T obj : inputList) {
            final Map<String, Object> map = JsonUtils.convertValue(obj,
                    new TypeReference<Map<String, Object>>() {
                    });
            if (CollectionUtils.isEmpty(map)) {
                log.error("Got null map, inputList: {}, clazz: {}", obj, clazz);
                continue;
            }
            valuesSubList = new ArrayList<>();
            for (final Object headerValue : headersList) {
                Object value = map.get(headerValue);
                valuesSubList.add(null != value ? value : EMPTY_STRING);
            }
            values.add(valuesSubList);
        }
        return values;
    }

    private List<Object> getHeadersList(final Class clazz) {
        if (null == clazz) {
            log.error("Got null clazz");
            return new ArrayList<>();
        }

        final Field[] fields = clazz.getDeclaredFields();
        final List<Object> headersList = new ArrayList<>();

        for (final Field field : fields) {
            final String name = field.getAnnotation(JsonProperty.class).value();
            headersList.add(name);
        }
        return headersList;
    }
}
