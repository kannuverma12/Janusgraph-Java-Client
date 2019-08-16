package com.paytm.digital.education.explore.service.helper;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.paytm.digital.education.dto.SftpConfig;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.explore.config.DataIngestionSftpConfig;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.service.SftpService;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import static com.mongodb.QueryOperators.OR;
import static com.paytm.digital.education.constant.SftpConstants.CHANNEL_TYPE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ATTRIBUTES;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.COMPONENT;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.KEY;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.NAMESPACE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.COURSES_DIRECTORY;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.COURSE_ENTITY;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.COURSES_FILE_NAME;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.DATA_INGESTION;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.EXAM_DIRECTORY;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.EXAM_ENTITY;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.EXAM_FILE_NAME;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.INCREMENTAL;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.INSTITUTION_DIRECTORY;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.INSTITUTE_ENTITY;
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
    private SftpService           sftpService;
    private PropertyReader        propertyReader;
    private CommonMongoRepository commonMongoRepository;

    public Map<String, Boolean> downloadFileFromSftp(String entity, Integer version) {
        log.info("Downloading Files from SFTP.");
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

        String currentExamFileName = null;
        String currentInstituteFileName = null;
        String currentCourseFileName = null;

        if (Objects.nonNull(version)) {
            switch (entity) {
                case EXAM_ENTITY:
                    currentExamFileName = MessageFormat.format(SFTP_EXAM_FILE_NAME_FORMAT, version);
                    break;
                case COURSE_ENTITY:
                    currentCourseFileName =
                            MessageFormat.format(SFTP_COURSE_FILE_NAME_FORMAT, version);
                    break;
                case INSTITUTE_ENTITY:
                    currentInstituteFileName =
                            MessageFormat.format(SFTP_INSTITUTE_FILE_NAME_FORMAT, version);
                    break;
                default:
                    throw new BadRequestException(ErrorEnum.INVALID_ENTITY_FOR_DATA_IMPORT,
                            ErrorEnum.INVALID_ENTITY_FOR_DATA_IMPORT.getExternalMessage());
            }
        } else {
            currentExamFileName = MessageFormat.format(SFTP_EXAM_FILE_NAME_FORMAT,
                    versioningData.get(NEXT_EXAM_FILE_VERSION));
            currentInstituteFileName = MessageFormat.format(SFTP_INSTITUTE_FILE_NAME_FORMAT,
                    versioningData.get(NEXT_INSTITUTE_FILE_VERSION));
            currentCourseFileName = MessageFormat.format(SFTP_COURSE_FILE_NAME_FORMAT,
                    versioningData.get(NEXT_COURSE_FILE_VERSION));
        }

        try {
            session = sftpService.createSession(sftpConfig);
            sftp = (ChannelSftp) session.openChannel(CHANNEL_TYPE);
            sftp.connect();
            if (Objects.nonNull(currentCourseFileName)) {
                log.info("Connected. Cd path : " + DataIngestionSftpConfig.getFilePath()
                        + COURSES_DIRECTORY);
                sftp.cd(DataIngestionSftpConfig.getFilePath() + COURSES_DIRECTORY);
                if (isFileExists(sftp, currentCourseFileName, version)) {
                    log.info("Found Course file with name {}", currentCourseFileName);
                    sftp.get(currentCourseFileName, COURSES_FILE_NAME);
                    fileExistFlags.put(COURSES_FILE_NAME, true);
                }
            }
            if (Objects.nonNull(currentInstituteFileName)) {
                log.info("Connected. Cd path : " + DataIngestionSftpConfig.getFilePath()
                        + INSTITUTION_DIRECTORY);
                sftp.cd(DataIngestionSftpConfig.getFilePath() + INSTITUTION_DIRECTORY);
                if (isFileExists(sftp, currentInstituteFileName, version)) {
                    log.info("Found Institute file with name {}", currentInstituteFileName);
                    sftp.get(currentInstituteFileName, INSTITUTE_FILE_NAME);
                    fileExistFlags.put(INSTITUTE_FILE_NAME, true);
                }
            }
            if (Objects.nonNull(currentExamFileName)) {
                log.info("Connected. Cd path : " + DataIngestionSftpConfig.getFilePath()
                        + EXAM_DIRECTORY);
                sftp.cd(DataIngestionSftpConfig.getFilePath() + EXAM_DIRECTORY);
                if (isFileExists(sftp, currentExamFileName, version)) {
                    log.info("Found Exam file with name {}", currentExamFileName);
                    sftp.get(currentExamFileName, EXAM_FILE_NAME);
                    fileExistFlags.put(EXAM_FILE_NAME, true);
                }
            }
        } catch (Exception e) {
            log.error("Sftp connection exception : " + e.getMessage());
            if (Objects.nonNull(version)) {
                throw new EducationException(ErrorEnum.SFTP_CONNECTION_FAILED,
                        ErrorEnum.SFTP_CONNECTION_FAILED.getExternalMessage());
            }
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

    private boolean isFileExists(ChannelSftp sftp, String fileName, Integer version) {
        try {
            Vector<ChannelSftp.LsEntry> list = sftp.ls(fileName);
        } catch (Exception e) {
            log.error("Sftp " + fileName + " retrieval exception : " + e.getMessage());
            if (Objects.nonNull(version)) {
                throw new BadRequestException(ErrorEnum.USER_DATA_DOESNOT_EXISTS,
                        ErrorEnum.USER_DATA_DOESNOT_EXISTS.getExternalMessage());
            }
            return false;
        }
        return true;
    }

    public <T> List<T> retrieveDataFromFile(String fileName, final Class<T> entryClass) {
        List<T> result = new ArrayList<>();
        try {
            String line;
            File initialFile = new File(fileName);
            InputStream stream = new FileInputStream(initialFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            while ((line = bufferedReader.readLine()) != null) {
                T convertedValue = JsonUtils.fromJson(line, entryClass);
                result.add(convertedValue);
            }
        } catch (IOException e) {
            log.error("Error retrieving data from file {}", fileName);
        }
        return result;
    }

    public <T> List<T> getExistingData(Class<T> entryClass, String entityField,
            List<Long> ids) {
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(entityField, ids);
        List<String> projectionFields = Arrays.asList(entityField, "_id");
        List<T> existingData = commonMongoRepository.findAll(queryObject,
                entryClass,
                projectionFields, OR);
        return existingData;
    }

    public void incrementFileVersion(String fieldName) {
        log.info("Incrementing file version for {}", fieldName);
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(COMPONENT, EXPLORE_COMPONENT);
        queryObject.put(NAMESPACE, DATA_INGESTION);
        queryObject.put(KEY, INCREMENTAL);
        List<String> fields = Arrays.asList(ATTRIBUTES);
        Update update = new Update();
        update.inc(fieldName, 1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true).upsert(true);
        commonMongoRepository.findAndModify(queryObject, update, options, Properties.class);
    }

}
