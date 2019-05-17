package com.paytm.digital.education.explore.controller;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.paytm.digital.education.explore.service.impl.ImageUploadServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(EDUCATION_BASE_URL)
public class FailedImagesUploadController {

    @Autowired
    private ImageUploadServiceImpl imageUploadService;
    
    @RequestMapping(method = RequestMethod.GET, path = "/v1/failedimagesupload")
    public String uploadFailedImages() {
        log.info("Entering uploadFailedImages");
        imageUploadService.uploadFailedImages();
        log.info("Exiting uploadFailedImages");
        return "success";
    }
}