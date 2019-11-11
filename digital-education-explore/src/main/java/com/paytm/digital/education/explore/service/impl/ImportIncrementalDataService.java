package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.explore.database.ingestion.Course;
import com.paytm.digital.education.explore.database.ingestion.Exam;
import com.paytm.digital.education.explore.dto.InstituteDto;
import com.paytm.digital.education.explore.dto.SchoolDto;
import com.paytm.digital.education.explore.response.dto.dataimport.DataImportResponse;
import com.paytm.digital.education.explore.service.helper.IncrementalDataHelper;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.COURSES_FILE_NAME;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.EXAM_FILE_NAME;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.INSTITUTE_FILE_NAME;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.SCHOOLS_FILE_NAME;

@AllArgsConstructor
@Service
public class ImportIncrementalDataService {

    private static Logger log = LoggerFactory.getLogger(ImportIncrementalDataService.class);

    private IncrementalDataHelper         incrementalDataHelper;
    private TransformAndSaveCourseService transformAndSaveCourseService;
    private TransformAndSaveExamService   transformAndSaveExamService;
    private TransformInstituteService     transformInstituteService;
    private TransformSchoolService        transformSchoolService;

    public List<DataImportResponse> importData(String entity, Integer version, Boolean versionUpdate) {
        List<DataImportResponse> dirList = new ArrayList<>();
        Map<String, Boolean> fileInfo = incrementalDataHelper.downloadFileFromSftp(entity, version);
        if (fileInfo.get(INSTITUTE_FILE_NAME)) {
            dirList.add(importInstituteData(version, versionUpdate));
        }
        if (fileInfo.get(EXAM_FILE_NAME)) {
            dirList.add(importExamData(version, versionUpdate));
        }
        if (fileInfo.get(COURSES_FILE_NAME)) {
            dirList.add(importCourseData(version, versionUpdate));
        }
        if (fileInfo.get(SCHOOLS_FILE_NAME)) {
            dirList.add(importSchoolData(version, versionUpdate));
        }
        return dirList;
    }

    private DataImportResponse importSchoolData(Integer version, Boolean versionUpdate) {
        DataImportResponse dataImportResponse = new DataImportResponse();
        log.info("-------------- Importing schools ---------------- ");
        try {
            List<SchoolDto> schoolDtos =
                    incrementalDataHelper.retrieveDataFromFile(SCHOOLS_FILE_NAME, SchoolDto.class);
            if (!schoolDtos.isEmpty()) {
                Integer updatedSchools = transformSchoolService.transformAndSaveSchoolsData(schoolDtos);
                String msg = "Imported " + updatedSchools + " schools.";
                log.info("DB import done for schools." + msg);
                dataImportResponse = updateDataIngestionResponse(dataImportResponse,
                        200, HttpStatus.OK, msg, "");
            }
        } catch (EducationException ee) {
            log.error("Error importing school dump : ", ee);
            dataImportResponse = updateDataIngestionResponse(dataImportResponse,
                    ee.getErrorEnum().getInternalCode(),
                    ee.getErrorEnum().getHttpStatus(),
                    ee.getErrorEnum().getExternalMessage(),
                    ee.getErrorEnum().getExternalMessage());
        }

        return dataImportResponse;
    }

    private DataImportResponse importCourseData(Integer version, Boolean versionUpdate) {
        log.info("-------------- Importing Courses ---------------- ");
        DataImportResponse dataImportResponse = new DataImportResponse();
        try {
            List<Course> courseDtos =
                    incrementalDataHelper.retrieveDataFromFile(COURSES_FILE_NAME, Course.class);
            if (!courseDtos.isEmpty()) {
                Integer updatedCourses = transformAndSaveCourseService.transformAndSave(courseDtos, versionUpdate);
                String msg = "Imported " + updatedCourses + " courses.";
                log.info("DB import done for course." + msg);
                dataImportResponse = updateDataIngestionResponse(dataImportResponse,
                        200, HttpStatus.OK, msg, "");
            } else {
                if (Objects.nonNull(version)) {
                    dataImportResponse = updateDataIngestionResponse(dataImportResponse,
                            ErrorEnum.CORRUPTED_FILE.getInternalCode(),
                            ErrorEnum.CORRUPTED_FILE.getHttpStatus(),
                            ErrorEnum.CORRUPTED_FILE.getExternalMessage(),
                            ErrorEnum.CORRUPTED_FILE.getExternalMessage());
                }
            }
        } catch (EducationException ee) {
            log.error("Error importing course dump : ", ee);
            dataImportResponse = updateDataIngestionResponse(dataImportResponse,
                    ee.getErrorEnum().getInternalCode(),
                    ee.getErrorEnum().getHttpStatus(),
                    ee.getErrorEnum().getExternalMessage(),
                    ee.getErrorEnum().getExternalMessage());
        }

        return dataImportResponse;
    }

    private DataImportResponse importExamData(Integer version, Boolean versionUpdate) {
        log.info("-------------- Importing Exams ---------------- ");
        DataImportResponse dataImportResponse = new DataImportResponse();
        try {
            List<Exam> examDtos = incrementalDataHelper.retrieveDataFromFile(EXAM_FILE_NAME,
                    Exam.class);
            if (!examDtos.isEmpty()) {
                Integer updatedExams = transformAndSaveExamService.transformAndSave(examDtos, versionUpdate);
                String msg = "Imported " + updatedExams + " exams.";
                log.info("DB import done for exams." + msg);
                dataImportResponse = updateDataIngestionResponse(dataImportResponse,
                        200, HttpStatus.OK, msg, "");
            } else {
                if (Objects.nonNull(version)) {
                    dataImportResponse = updateDataIngestionResponse(dataImportResponse,
                            ErrorEnum.CORRUPTED_FILE.getInternalCode(),
                            ErrorEnum.CORRUPTED_FILE.getHttpStatus(),
                            ErrorEnum.CORRUPTED_FILE.getExternalMessage(),
                            ErrorEnum.CORRUPTED_FILE.getExternalMessage());
                }
            }
        } catch (EducationException ee) {
            log.error("Error importing exam dump : ", ee);
            dataImportResponse = updateDataIngestionResponse(dataImportResponse,
                    ee.getErrorEnum().getInternalCode(),
                    ee.getErrorEnum().getHttpStatus(),
                    ee.getErrorEnum().getExternalMessage(),
                    ee.getErrorEnum().getExternalMessage());
        }
        return dataImportResponse;
    }

    private DataImportResponse importInstituteData(Integer version, Boolean versionUpdate) {
        log.info("-------------- Importing Institutes ---------------- ");
        DataImportResponse dataImportResponse = new DataImportResponse();
        try {
            List<InstituteDto> instituteDtos = incrementalDataHelper
                    .retrieveDataFromFile(INSTITUTE_FILE_NAME, InstituteDto.class);
            if (!instituteDtos.isEmpty()) {
                Integer institutesUpdated =
                        transformInstituteService
                                .transformAndSaveInstituteData(instituteDtos, versionUpdate);
                log.info("Imported " + institutesUpdated + " institutes.");

                dataImportResponse = updateDataIngestionResponse(dataImportResponse,
                        200, HttpStatus.OK,
                        "Imported " + institutesUpdated + " institutes.", "");
            } else {
                if (Objects.nonNull(version)) {
                    dataImportResponse = updateDataIngestionResponse(dataImportResponse,
                            ErrorEnum.CORRUPTED_FILE.getInternalCode(),
                            ErrorEnum.CORRUPTED_FILE.getHttpStatus(),
                            ErrorEnum.CORRUPTED_FILE.getExternalMessage(),
                            ErrorEnum.CORRUPTED_FILE.getExternalMessage());
                }
            }
        } catch (EducationException ee) {
            log.error("Error importing institute dump : ", ee);
            dataImportResponse = updateDataIngestionResponse(dataImportResponse,
                    ee.getErrorEnum().getInternalCode(),
                    ee.getErrorEnum().getHttpStatus(),
                    ee.getErrorEnum().getExternalMessage(),
                    ee.getInternalMessage());
        }

        return dataImportResponse;
    }

    private DataImportResponse updateDataIngestionResponse(DataImportResponse dataImportResponse,
            int statusCode, HttpStatus httpStatus, String message, String errorMessage) {
        dataImportResponse.setStatusCode(statusCode);
        dataImportResponse.setHttpStatus(httpStatus);
        dataImportResponse.setMessage(message);
        dataImportResponse.setError(errorMessage);
        return dataImportResponse;
    }
}
