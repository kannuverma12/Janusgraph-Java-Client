package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.explore.utility.CommonUtil;
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

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@RestController
@Validated
@RequestMapping(EDUCATION_BASE_URL)
public class CampusEngagementController {
    public static final String SAMPLE_XLSX_FILE_PATH =
            "/home/gauravkumardas/Documents/CampusAmbasador.xlsx";

    @RequestMapping(method = RequestMethod.POST, path = "/auth/v1/campus-engagement/upload")
    public @ResponseBody Map<String, Object> getExamById(@RequestParam("file") MultipartFile file)
            throws IOException,
            InvalidFormatException {
        return CommonUtil.readDataFromExcel(SAMPLE_XLSX_FILE_PATH);
    }
}
