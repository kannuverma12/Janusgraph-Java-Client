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


    @Scheduled(fixedDelay = 900000)
    @SchedulerLock(name = "imagesUploadScheduler")
    public void imagesUploadScheduler() {

        CronProperties isImageUploadCronProperty =
                cronPropertiesRepository.findByKey(ExploreConstants.IMAGE_UPLOAD_CRON_KEY);
        if (BooleanUtils.isTrue((Boolean) isImageUploadCronProperty.getValue())) {
            log.info("entered in imagesUploadScheduler");
            imageUploadServiceImpl.uploadImages();
            log.info("exited from imagesUploadScheduler");
        }
    }

    @Scheduled(fixedDelay = 3600000, initialDelay = 600000)
    @SchedulerLock(name = "failedImagesUploadScheduler")
    public void failedImagesUploadScheduler() {
        CronProperties isFailedImageCronProperty =
                cronPropertiesRepository.findByKey(ExploreConstants.FAILED_IMAGE_CRON_KEY);
        if (BooleanUtils.isTrue((Boolean) isFailedImageCronProperty.getValue())) {
            log.info("entered in imagesUploadScheduler");
            imageUploadServiceImpl.uploadFailedImages();
            log.info("exited from imagesUploadScheduler");
        }
    }
}
