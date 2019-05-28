package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.explore.database.entity.CampusAmbassador;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

public interface ImportDataService {
    public Map<Long, List<CampusAmbassador>> importCampusEngagementData(MultipartFile file) throws IOException,
            GeneralSecurityException;
}
