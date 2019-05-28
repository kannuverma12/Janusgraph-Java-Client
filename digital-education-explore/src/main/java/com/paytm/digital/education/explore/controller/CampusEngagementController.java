package com.paytm.digital.education.explore.controller;

import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.XLS;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.XLSX;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_COURSE_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXCEL_FILE_EXTENSION;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_FILE_NAME;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.database.entity.CampusAmbassador;
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
import java.security.GeneralSecurityException;
import java.util.List;
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
    public @ResponseBody Map<Long, List<CampusAmbassador>> upload(@RequestParam("file") MultipartFile file)
            throws IOException, GeneralSecurityException {
        String fileName = file.getOriginalFilename();
        if (!fileName.endsWith(XLS) && !fileName.endsWith(XLSX)) {
            throw new BadRequestException(INVALID_EXCEL_FILE_EXTENSION,
                    INVALID_EXCEL_FILE_EXTENSION.getExternalMessage());
        } else if (fileName.contains("..")) {
            throw new BadRequestException(INVALID_FILE_NAME,
                    INVALID_FILE_NAME.getExternalMessage() + fileName);
        }
        return importDataService.importCampusEngagementData(file);
    }
}
