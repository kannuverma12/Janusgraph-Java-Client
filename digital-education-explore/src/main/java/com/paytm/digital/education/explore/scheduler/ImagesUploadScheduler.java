package com.paytm.digital.education.explore.scheduler;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import com.paytm.digital.education.explore.constants.ExploreConstants;
import com.paytm.digital.education.explore.database.entity.CronProperties;
import com.paytm.digital.education.explore.database.repository.CronPropertiesRepository;
import com.paytm.digital.education.explore.service.impl.ImageUploadServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;

@Slf4j
@Configuration
@EnableScheduling
public class ImagesUploadScheduler {

    @Autowired
    private ImageUploadServiceImpl   imageUploadServiceImpl;

    @Autowired
    private CronPropertiesRepository cronPropertiesRepository;
    
    @Scheduled(fixedDelayString = "${image.upload.cron.fixed.delay}")
    @SchedulerLock(name = "imagesUploadScheduler")
    public void imagesUploadScheduler() {

        CronProperties imageUploadCronProperty =
                cronPropertiesRepository.findByCronName(ExploreConstants.IMAGE_UPLOAD_CRON_KEY);
        if (BooleanUtils.isTrue(imageUploadCronProperty.getIsActive())) {
            log.info("entered in imagesUploadScheduler");
            imageUploadServiceImpl.uploadImages();
            log.info("exited from imagesUploadScheduler");
        }
    }

    @Scheduled(fixedDelayString = "${failed.image.cron.fixed.delay}", initialDelayString = "${failed.image.cron.initial.delay}")
    @SchedulerLock(name = "failedImagesUploadScheduler")
    public void failedImagesUploadScheduler() {
        CronProperties isFailedImageCronProperty =
                cronPropertiesRepository.findByCronName(ExploreConstants.FAILED_IMAGE_CRON_KEY);
        if (BooleanUtils.isTrue(isFailedImageCronProperty.getIsActive())) {
            log.info("entered in imagesUploadScheduler");
            imageUploadServiceImpl.uploadFailedImages();
            log.info("exited from imagesUploadScheduler");
        }
    }
}
