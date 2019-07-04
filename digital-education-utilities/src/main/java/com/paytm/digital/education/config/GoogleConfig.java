package com.paytm.digital.education.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GoogleConfig {
    private static String exploreCredentialFolderPath;
    private static String coachingCredentialFolderPath;
    private static String campusCredentialFileName;
    private static String coachingCredentialFileName;

    @Value("${google.api.explore.credential.folderpath}")
    public void setExploreCredentialFolderPath(String path) {
        exploreCredentialFolderPath = path;
    }

    @Value("${google.api.coaching.credential.folderpath}")
    public void setCoachingCredentialFolderPath(String path) {
        coachingCredentialFolderPath = path;
    }

    @Value("${google.api.campus.credential.filename}")
    public void setCampusCredentialFileName(String fileName) {
        campusCredentialFileName = fileName;
    }

    @Value("${google.api.coaching.credential.filename}")
    public void setCoachingCredentialFile(String fileName) {
        coachingCredentialFileName = fileName;
    }

    public static String getExploreCredentialFolderPath() {
        return exploreCredentialFolderPath;
    }

    public static String getCoachingCredentialFolderPath() {
        return coachingCredentialFolderPath;
    }

    public static String getCampusCredentialFileName() {
        return campusCredentialFileName;
    }

    public static String getCoachingCredentialFileName() {
        return coachingCredentialFileName;
    }
}
