package com.paytm.digital.education.explore.scheduler;

import com.paytm.digital.education.database.entity.CronProperties;
import com.paytm.digital.education.explore.database.repository.CronPropertiesRepository;
import com.paytm.digital.education.explore.response.dto.dataimport.DataImportResponse;
import com.paytm.digital.education.explore.service.impl.ImportIncrementalDataService;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.DATA_INGESTION_IMPORT;


@AllArgsConstructor
@Component
public class DataIngestionScheduler {

    private static Logger log = LoggerFactory.getLogger(DataIngestionScheduler.class);

    private ImportIncrementalDataService importIncrementalDataService;
    private CronPropertiesRepository     cronPropertiesRepository;

    public List<DataImportResponse> importDataScheduler() {
        CronProperties dataIngestionCronProperty =
                cronPropertiesRepository.findByCronName(DATA_INGESTION_IMPORT);

        if (Objects.isNull(dataIngestionCronProperty)) {
            log.info("No such scheduler exists with name {}", DATA_INGESTION_IMPORT);
        } else {
            if (BooleanUtils.isTrue(dataIngestionCronProperty.getIsActive())) {
                log.info("Starting Data Ingestion Import via scheduler");
                return importIncrementalDataService.importData(null, null, null);

            }
        }
        return Collections.emptyList();
    }

}
