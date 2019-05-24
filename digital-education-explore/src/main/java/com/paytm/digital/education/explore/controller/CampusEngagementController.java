package com.paytm.digital.education.explore.controller;

import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.XLS;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.XLSX;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

import com.paytm.digital.education.explore.service.ImportDataService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@RestController
@Validated
@RequestMapping(EDUCATION_BASE_URL)
public class CampusEngagementController {
    private ImportDataService importDataService;

    @RequestMapping(method = RequestMethod.POST, path = "/v1/campus-engagement-data/upload")
    public @ResponseBody Map<String, Object> upload(@RequestParam("file") MultipartFile file)
            throws IOException {
        String fileName = file.getOriginalFilename();
        if (!fileName.endsWith(XLS) && !fileName.endsWith(XLSX)) {
            throw new IllegalArgumentException(
                    "Received file does not have a standard excel extension.");
        } else if (fileName.contains("..")) {

        }
        return importDataService.importCampusEngagementData(file);
    }
}
