package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.explore.service.impl.ImportIncrementalDataService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(EDUCATION_BASE_URL)
public class DataIngestionController {
    private ImportIncrementalDataService importIncrementalDataService;

    @RequestMapping(method = RequestMethod.GET, path = "/v1/import/data")
    public @ResponseBody boolean importData() throws java.io.FileNotFoundException {
        importIncrementalDataService.importData();
        return true;
    }
}
