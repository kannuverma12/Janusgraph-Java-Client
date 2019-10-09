package com.paytm.digital.education.ingestion.helper;

import static com.paytm.digital.education.ingestion.constant.IngestionConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.ingestion.constant.IngestionConstants.EXPLORE_DATA_INGEST;
import static com.paytm.digital.education.ingestion.constant.IngestionConstants.GOOGLE_SHEET_INFO;
import static com.paytm.digital.education.mapping.ErrorEnum.BLANK_SHEET_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.SHEET_INFO_NOT_DEFINED;

import com.paytm.digital.education.database.entity.FailedData;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.ingestion.request.DataExportPropertiesRequest;
import com.paytm.digital.education.ingestion.request.DataImportPropertiesRequest;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.utility.DateUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class DataIngestHelper {

    private static Logger log = LoggerFactory.getLogger(DataIngestHelper.class);

    private final PropertyReader propertyReader;

    public Map<String, java.lang.Object> getDataImportProperties(final DataImportPropertiesRequest request,
            final Class clazz) {

        if (Objects.isNull(request) || StringUtils.isBlank(request.getSheetIdKey())) {
            log.error("Got null/empty exportData properties request for form : {}.",
                    clazz.getName());
            throw new BadRequestException(BLANK_SHEET_ID, BLANK_SHEET_ID.getExternalMessage());
        }
        return getProperties(clazz);
    }

    public Map<String, java.lang.Object> getDataExportProperties(final DataExportPropertiesRequest request,
            final Class clazz) {
        if (Objects.isNull(request) || StringUtils.isBlank(request.getSheetIdKey())) {
            log.error("Got null/empty exportData properties request for form : {}.",
                    clazz.getName());
            throw new BadRequestException(BLANK_SHEET_ID, BLANK_SHEET_ID.getExternalMessage());
        }
        return getProperties(clazz);
    }

    private Map<String, java.lang.Object> getProperties(final Class clazz) {
        Map<String, java.lang.Object> properties = propertyReader
                .getPropertiesAsMapByKey(EXPLORE_COMPONENT, GOOGLE_SHEET_INFO, EXPLORE_DATA_INGEST);
        if (CollectionUtils.isEmpty(properties)) {
            throw new EducationException(SHEET_INFO_NOT_DEFINED,
                    SHEET_INFO_NOT_DEFINED.getExternalMessage(), new java.lang.Object[] {clazz.getName()});
        }
        return properties;
    }

    public void addToFailedList(Object object, String message,
            boolean isImportable, List<java.lang.Object> failedDataList, String component, String type) {
        FailedData failedData = new FailedData();
        failedData.setComponent(component);
        failedData.setHasImported(false);
        failedData.setType(type);
        failedData.setMessage(message);
        failedData.setIsImportable(isImportable);
        failedData.setFailedDate(DateUtil.getCurrentDate());
        failedData.setData(object);
        failedDataList.add(failedData);
    }
}
