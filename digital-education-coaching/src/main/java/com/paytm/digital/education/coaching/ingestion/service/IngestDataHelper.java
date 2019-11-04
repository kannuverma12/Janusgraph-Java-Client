package com.paytm.digital.education.coaching.ingestion.service;

import com.paytm.digital.education.database.entity.FailedData;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.utility.DateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DATA_INGEST;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.GOOGLE_SHEETS_INFO;

@Slf4j
@Service
@AllArgsConstructor
public class IngestDataHelper {

    private final PropertyReader propertyReader;

    public Map<String, Object> getDataIngestionProperties() {
        return propertyReader
                .getPropertiesAsMapByKey(COACHING_COMPONENT, GOOGLE_SHEETS_INFO, DATA_INGEST);
    }

    public void addToFailedList(Object object, String message,
            boolean isImportable, List<Object> failedDataList, String component, String type) {
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
