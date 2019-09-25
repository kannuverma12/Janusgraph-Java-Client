package com.paytm.digital.education.explore.scheduler;

import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.CAMPUS_ENGAGEMENT_FAILED_IMPORT;

import com.paytm.digital.education.explore.service.impl.ImportAmbassadorServiceImpl;
import com.paytm.digital.education.explore.service.impl.ImportArticleServiceImpl;
import com.paytm.digital.education.explore.service.impl.ImportEventServiceImpl;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.paytm.digital.education.explore.database.entity.CronProperties;
import com.paytm.digital.education.explore.database.repository.CronPropertiesRepository;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.Objects;


@AllArgsConstructor
@Configuration
@EnableScheduling
public class CampusEngagementScheduler {

    private static Logger log = LoggerFactory.getLogger(CampusEngagementScheduler.class);

    private ImportArticleServiceImpl    importArticleService;
    private ImportEventServiceImpl      importEventService;
    private ImportAmbassadorServiceImpl importAmbassadorService;
    private CronPropertiesRepository    cronPropertiesRepository;

    //@Scheduled(fixedDelayString = "${failed-campus-engagement.import.cron.fixed.delay}")
    //@SchedulerLock(name = "importFailedCampusEngagementData")
    public void importFailedArticleScheduler() throws IOException, ParseException,
            GeneralSecurityException {
        CronProperties campusEngagementCronPropety =
                cronPropertiesRepository
                        .findByCronName(CAMPUS_ENGAGEMENT_FAILED_IMPORT);
        if (Objects.isNull(campusEngagementCronPropety)) {
            log.info("No such scheduler exists with name {}", CAMPUS_ENGAGEMENT_FAILED_IMPORT);
        } else {
            if (BooleanUtils.isTrue(campusEngagementCronPropety.getIsActive())) {
                log.info("entered in reimport article scheduler");
                //importArticleService.importData(true);
                log.info("exited in reimport article scheduler");

                log.info("entered in reimport ambassador scheduler scheduler");
                //importAmbassadorService.importData(true);
                log.info("exited in reimport ambassador scheduler scheduler");

                log.info("entered in reimport event scheduler scheduler");
                //importEventService.importData(true);
                log.info("exited in reimport event scheduler scheduler");
            }
        }
    }
}
