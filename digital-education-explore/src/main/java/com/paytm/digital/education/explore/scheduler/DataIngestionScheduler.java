package com.paytm.digital.education.explore.scheduler;

import com.paytm.digital.education.database.entity.CronProperties;
import com.paytm.digital.education.explore.database.repository.CronPropertiesRepository;
import com.paytm.digital.education.explore.service.impl.ImportIncrementalDataService;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;

import net.javacrumbs.shedlock.core.SchedulerLock;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.DATA_INGESTION_IMPORT;


@AllArgsConstructor
//@Configuration
//@EnableScheduling
//@Profile({"dev", "staging", "production"})
@Component
public class DataIngestionScheduler {

    private static Logger log = LoggerFactory.getLogger(DataIngestionScheduler.class);

    private ImportIncrementalDataService importIncrementalDataService;
    private CronPropertiesRepository     cronPropertiesRepository;

    //@Scheduled(fixedDelayString = "${data-ingestion.import.cron.fixed.delay}")
    //@SchedulerLock(name = "dataIngestionImport")
    public void importDataScheduler() {
        CronProperties dataIngestionCronProperty =
                cronPropertiesRepository.findByCronName(DATA_INGESTION_IMPORT);

        if (Objects.isNull(dataIngestionCronProperty)) {
            log.info("No such scheduler exists with name {}", DATA_INGESTION_IMPORT);
        } else {
            if (BooleanUtils.isTrue(dataIngestionCronProperty.getIsActive())) {
                log.info("Starting Data Ingestion Import via scheduler");
                importIncrementalDataService.importData(null, null, null);
                log.info("Finished Data Ingestion Import via scheduler");

            }
        }
    }

}
