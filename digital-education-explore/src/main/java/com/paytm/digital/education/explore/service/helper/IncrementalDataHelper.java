package com.paytm.digital.education.explore.service.helper;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.paytm.digital.education.dto.SftpConfig;
import com.paytm.digital.education.explore.config.DataIngestionSftpConfig;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.service.SftpService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static com.paytm.digital.education.constant.SftpConstants.CHANNEL_TYPE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.COURSES_FILE_NAME;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.DATA_INGESTION;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.EXAM_FILE_NAME;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.INCREMENTAL;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.INSTITUTE_FILE_NAME;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.NEXT_COURSE_FILE_VERSION;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.NEXT_EXAM_FILE_VERSION;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.NEXT_INSTITUTE_FILE_VERSION;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.SFTP_COURSE_FILE_NAME_FORMAT;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.SFTP_EXAM_FILE_NAME_FORMAT;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.SFTP_INSTITUTE_FILE_NAME_FORMAT;

@Slf4j
@Service
@AllArgsConstructor
public class IncrementalDataHelper {
    private SftpService    sftpService;
    private PropertyReader propertyReader;

    public Map<String, Boolean> downloadFileFromSftp() {
        SftpConfig sftpConfig = new SftpConfig();
        sftpConfig.setUsername(DataIngestionSftpConfig.getUsername());
        sftpConfig.setHost(DataIngestionSftpConfig.getHost());
        sftpConfig.setPort(DataIngestionSftpConfig.getPort());
        sftpConfig.setKeyPath(DataIngestionSftpConfig.getKeyPath());
        ChannelSftp sftp = null;
        Session session = null;
        Map<String, Boolean> fileExistFlags = new HashMap<>();
        fileExistFlags.put(EXAM_FILE_NAME, false);
        fileExistFlags.put(INSTITUTE_FILE_NAME, false);
        fileExistFlags.put(COURSES_FILE_NAME, false);
        Map<String, Object> versioningData = propertyReader
                .getPropertiesAsMapByKey(EXPLORE_COMPONENT, DATA_INGESTION, INCREMENTAL);

        String currentExamFileName = MessageFormat.format(SFTP_EXAM_FILE_NAME_FORMAT,
                versioningData.get(NEXT_EXAM_FILE_VERSION));
        String currentInstituteFileName = MessageFormat.format(SFTP_INSTITUTE_FILE_NAME_FORMAT,
                versioningData.get(NEXT_INSTITUTE_FILE_VERSION));
        String currentCourseFileName = MessageFormat.format(SFTP_COURSE_FILE_NAME_FORMAT,
                versioningData.get(NEXT_COURSE_FILE_VERSION));
        try {
            session = sftpService.createSession(sftpConfig);
            sftp = (ChannelSftp) session.openChannel(CHANNEL_TYPE);
            sftp.connect();
            sftp.cd(DataIngestionSftpConfig.getFilePath());
            if (isFileExists(sftp, currentCourseFileName)) {
                sftp.get(currentCourseFileName, COURSES_FILE_NAME);
                fileExistFlags.put(COURSES_FILE_NAME, true);
            }
            if (isFileExists(sftp, currentInstituteFileName)) {
                sftp.get(currentInstituteFileName, INSTITUTE_FILE_NAME);
                fileExistFlags.put(INSTITUTE_FILE_NAME, true);
            }
            if (isFileExists(sftp, currentExamFileName)) {
                sftp.get(currentExamFileName, EXAM_FILE_NAME);
                fileExistFlags.put(EXAM_FILE_NAME, true);
            }
        } catch (Exception e) {
            log.info("Sftp connection exception : " + e.getMessage());
        } finally {
            if (sftp != null) {
                sftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
        return fileExistFlags;
    }

    private boolean isFileExists(ChannelSftp sftp, String fileName) {
        try {
            Vector<ChannelSftp.LsEntry> list = sftp.ls(fileName);
        } catch (Exception e) {
            log.info("Sftp " + fileName + " retrieval exception : " + e.getMessage());
            return false;
        }
        return true;
    }
}
