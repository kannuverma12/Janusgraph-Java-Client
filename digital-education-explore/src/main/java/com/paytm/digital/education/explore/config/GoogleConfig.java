package com.paytm.digital.education.explore.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GoogleConfig {
    private static String credentialFolderPath;
    private static String credentialFileName;

    @Value("${google.api.credential.folderpath}")
    public void setCredentialFolderPath(String path) {
        credentialFolderPath = path;
    }

    @Value("${google.api.credential.filename}")
    public void setServiceEndPoint(String fileName) {
        credentialFileName = fileName;
    }

    public static String getCredentialFolderPath() {
        return credentialFolderPath;
    }

    public static String getCredentialFileName() {
        return credentialFileName;
    }
}
