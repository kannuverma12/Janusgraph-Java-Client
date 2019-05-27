package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.service.ImportDataService;
import com.paytm.digital.education.explore.utility.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class ImportDataServiceImpl implements ImportDataService {
    public Map<String, Object> importCampusEngagementData(MultipartFile file)
            throws IOException, GeneralSecurityException {
        Map<String, Object> campusEngagementData = CommonUtil.readDataFromExcel(file);
        //GoogleDrive.getAllFiles();
        return campusEngagementData;
    }
}
