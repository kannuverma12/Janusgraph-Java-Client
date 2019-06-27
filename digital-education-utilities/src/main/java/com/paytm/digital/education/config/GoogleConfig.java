package com.paytm.digital.education.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GoogleConfig {
    private static String credentialFolderPath;
    private static String campusCredentialFileName;
    private static String coachingCredentialFileName;

    @Value("${google.api.credential.folderpath}")
    public void setCredentialFolderPath(String path) {
        credentialFolderPath = path;
    }

    @Value("${google.api.campus.credential.filename}")
    public void setCampusCredentialFileName(String fileName) {
        campusCredentialFileName = fileName;
    }

    @Value("${google.api.coaching.credential.filename}")
    public void setCoachingCredentialFile(String fileName) {
        coachingCredentialFileName = fileName;
    }

    public static String getCredentialFolderPath() {
        return credentialFolderPath;
    }

    public static String getCampusCredentialFileName() {
        return campusCredentialFileName;
    }

    public static String getCoachingCredentialFileName() {
        return coachingCredentialFileName;
    }
}
