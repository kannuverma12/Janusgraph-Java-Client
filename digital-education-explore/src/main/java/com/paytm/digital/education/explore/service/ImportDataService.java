package com.paytm.digital.education.explore.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface ImportDataService {
    public Map<String, Object> importCampusEngagementData(MultipartFile file) throws IOException;
}
