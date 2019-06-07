package com.paytm.digital.education.explore.scheduler;

import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.CAMPUS_ENGAGEMENT_FAILED_IMPORT;

import com.paytm.digital.education.explore.service.impl.ImportAmbassadorServiceImpl;
import com.paytm.digital.education.explore.service.impl.ImportArticleServiceImpl;
import com.paytm.digital.education.explore.service.impl.ImportEventServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import com.paytm.digital.education.explore.database.entity.CronProperties;
import com.paytm.digital.education.explore.database.repository.CronPropertiesRepository;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;

import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Configuration
@EnableScheduling
public class CampusEngagementScheduler {

    private ImportArticleServiceImpl    importArticleService;
    private ImportEventServiceImpl      importEventService;
    private ImportAmbassadorServiceImpl importAmbassadorService;
    private CronPropertiesRepository    cronPropertiesRepository;

    @Scheduled(fixedDelayString = "${failed-campus-engagement.import.cron.fixed.delay}")
    @SchedulerLock(name = "importFailedCampusEngagementData")
    public void importFailedArticleScheduler() {
        CronProperties campusEngagementCronPropety =
                cronPropertiesRepository
                        .findByCronName(CAMPUS_ENGAGEMENT_FAILED_IMPORT);
        if (Objects.isNull(campusEngagementCronPropety)) {
            log.info("No such scheduler exists with name {}", CAMPUS_ENGAGEMENT_FAILED_IMPORT);
        } else {
            if (BooleanUtils.isTrue(campusEngagementCronPropety.getIsActive())) {
                log.info("entered in reimport article scheduler");
                importArticleService.reimportFailedArticles();
                log.info("exited in reimport article scheduler");

                log.info("entered in reimport ambassador scheduler scheduler");
                importAmbassadorService.reimportFailedAmbassador();
                log.info("exited in reimport ambassador scheduler scheduler");

                log.info("entered in reimport event scheduler scheduler");
                importEventService.reimportFailedEvents();
                log.info("exited in reimport event scheduler scheduler");
            }
        }
    }
}
