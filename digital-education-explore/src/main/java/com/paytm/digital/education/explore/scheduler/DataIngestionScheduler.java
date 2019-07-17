package com.paytm.digital.education.explore.scheduler;

import com.paytm.digital.education.explore.database.entity.CronProperties;
import com.paytm.digital.education.explore.database.repository.CronPropertiesRepository;
import com.paytm.digital.education.explore.service.impl.ImportIncrementalDataService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Objects;

import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.DATA_INGESTION_IMPORT;

@Slf4j
@AllArgsConstructor
@Configuration
@EnableScheduling
public class DataIngestionScheduler {

    private ImportIncrementalDataService importIncrementalDataService;
    private CronPropertiesRepository     cronPropertiesRepository;

    @Scheduled(fixedDelayString = "${data-ingestion.import.cron.fixed.delay}")
    @SchedulerLock(name = "dataIngestionImport")
    public void importFailedArticleScheduler() {
        CronProperties dataIngestionCronProperty =
                cronPropertiesRepository.findByCronName(DATA_INGESTION_IMPORT);

        if (Objects.isNull(dataIngestionCronProperty)) {
            log.info("No such scheduler exists with name {}", DATA_INGESTION_IMPORT);
        } else {
            if (BooleanUtils.isTrue(dataIngestionCronProperty.getIsActive())) {
                log.info("Starting Data Ingestion Import via scheduler");
                importIncrementalDataService.importData();
                log.info("Finished Data Ingestion Import via scheduler");

            }
        }
    }

}
