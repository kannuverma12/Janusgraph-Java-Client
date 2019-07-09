package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.service.helper.IncrementalDataHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@AllArgsConstructor
@Service
@Slf4j
public class ImportIncrementalDataService {
    private IncrementalDataHelper incrementalDataHelper;

    public boolean importData() {
        Map<String, Boolean> fileInfo = incrementalDataHelper.downloadFileFromSftp();
        System.out.print(fileInfo);
        return true;
    }
}
