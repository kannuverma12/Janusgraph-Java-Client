package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.dto.SftpConfigData;
import com.paytm.digital.education.service.SftpService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class DataIngestionController {
    private SftpService sftpService;

    @RequestMapping(method = RequestMethod.GET, path = "/v1/import/institute")
    public @ResponseBody boolean importCoachingFacilities()
            throws IOException, GeneralSecurityException {
        SftpConfigData sftpConfigData = new SftpConfigData();
        sftpConfigData.setHost("10.20.43.219");
        sftpConfigData.setPort(22);
        sftpConfigData.setUsername("sftpuser");
        sftpConfigData.setKeyPath("/home/gauravkumardas/Downloads/sftp.pem");
        sftpService.createSession(sftpConfigData);
        return true;
    }
}
