package com.paytm.digital.education.ingestion.service.exportdata;


import static com.paytm.digital.education.ingestion.constant.IngestionConstants.EMPTY_STRING;
import static com.paytm.digital.education.ingestion.constant.IngestionConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.ingestion.constant.IngestionConstants.EXPLORE_DATA_INGEST;
import static com.paytm.digital.education.ingestion.constant.IngestionConstants.GOOGLE_SHEET_INFO;
import static com.paytm.digital.education.mapping.ErrorEnum.BLANK_SHEET_ID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.paytm.digital.education.config.GoogleConfig;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.ingestion.helper.DataIngestHelper;
import com.paytm.digital.education.ingestion.helper.GoogleSheetHelper;
import com.paytm.digital.education.ingestion.request.DataExportPropertiesRequest;
import com.paytm.digital.education.ingestion.response.DataExportPropertiesResponse;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.utility.GoogleDriveUtil;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public abstract class AbstractExportService {

    private static Logger log = LoggerFactory.getLogger(AbstractExportService.class);

    @Autowired
    protected DataIngestHelper ingestDataHelper;

    protected DataExportPropertiesResponse getProperties(DataExportPropertiesRequest request, final Class clazz) {
        final Map<String, Object> propertyMap = this.ingestDataHelper.getDataExportProperties(request, clazz);
        try {
            final String sheetId = (String) propertyMap.get(request.getSheetIdKey());
            log.debug("Got: sheetId: {}", sheetId);
            return DataExportPropertiesResponse.builder().sheetId(sheetId).build();
        } catch (final NullPointerException e) {
            log.error("Got exception for exportData, propertiesRequest: {}, exception: ",
                    e, request);
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
                "A1:" + GoogleSheetHelper.convertNumberToA1Notation(values.get(0).size());
        log.debug("Clazz: {}, Range: {}", clazz, range);

        try {
            UpdateValuesResponse updateValuesResponse = GoogleDriveUtil.writeDataToSheet(
                    sheetId, range, values, GoogleConfig.getExploreCredentialFileName(),
                    GoogleConfig.getExploreCredentialFolderPath());
            log.debug("Export response : {}", JsonUtils.toJson(updateValuesResponse));
            return updateValuesResponse.getUpdatedRows();
        } catch (final IOException | GeneralSecurityException e) {
            log.error("Got exception in processRecords, list: {}, clazz:{}, sheetId: {}, "
                    + "range: {}, exception: ",e, list, clazz, sheetId, range);
            return 0;
        }
    }

    private <T> List<List<Object>> convertListToObjectList(
            final List<T> inputList, final Class clazz) {

        final List<List<Object>> values = new ArrayList<>();
        if (CollectionUtils.isEmpty(inputList) || null == clazz) {
            log.error("Got Empty inputList or null clazz, inputList: {}, clazz: {}",
                    inputList, clazz);
            return values;
        }

        final List<Object> headerKeysList = GoogleSheetHelper.getHeaderKeysList(clazz);
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
