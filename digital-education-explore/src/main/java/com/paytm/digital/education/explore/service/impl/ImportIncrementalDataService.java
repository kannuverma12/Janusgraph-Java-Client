package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.explore.database.ingestion.Course;
import com.paytm.digital.education.explore.database.ingestion.Exam;
import com.paytm.digital.education.explore.dto.InstituteDto;
import com.paytm.digital.education.explore.service.helper.IncrementalDataHelper;
import com.paytm.digital.education.mapping.ErrorEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.COURSES_FILE_NAME;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.EXAM_FILE_NAME;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.INSTITUTE_FILE_NAME;

@AllArgsConstructor
@Service
@Slf4j
public class ImportIncrementalDataService {
    private IncrementalDataHelper         incrementalDataHelper;
    private TransformAndSaveCourseService transformAndSaveCourseService;
    private TransformAndSaveExamService   transformAndSaveExamService;
    private TransformInstituteService     transformInstituteService;

    public boolean importData(String entity, Integer version, Boolean versionUpdate) {
        Map<String, Boolean> fileInfo = incrementalDataHelper.downloadFileFromSftp(entity, version);
        if (fileInfo.get(INSTITUTE_FILE_NAME)) {
            List<InstituteDto> instituteDtos = incrementalDataHelper
                    .retrieveDataFromFile(INSTITUTE_FILE_NAME, InstituteDto.class);
            if (!instituteDtos.isEmpty()) {
                Integer institutesUpdated =
                        transformInstituteService
                                .transformAndSaveInstituteData(instituteDtos, versionUpdate);
                log.info("Imported " + institutesUpdated + " institutes.");
            } else {
                if (Objects.nonNull(version)) {
                    throw new EducationException(ErrorEnum.CORRUPTED_FILE,
                            ErrorEnum.CORRUPTED_FILE.getExternalMessage());
                }
            }
        }
        if (fileInfo.get(EXAM_FILE_NAME)) {
            List<Exam> examDtos = incrementalDataHelper.retrieveDataFromFile(EXAM_FILE_NAME,
                    Exam.class);
            if (!examDtos.isEmpty()) {
                transformAndSaveExamService.transformAndSave(examDtos, versionUpdate);
                log.info("Imported " + examDtos.size() + " exams.");
            } else {
                if (Objects.nonNull(version)) {
                    throw new EducationException(ErrorEnum.CORRUPTED_FILE,
                            ErrorEnum.CORRUPTED_FILE.getExternalMessage());
                }
            }
        }
        if (fileInfo.get(COURSES_FILE_NAME)) {
            List<Course> courseDtos =
                    incrementalDataHelper.retrieveDataFromFile(COURSES_FILE_NAME, Course.class);
            if (!courseDtos.isEmpty()) {
                transformAndSaveCourseService.transformAndSave(courseDtos, versionUpdate);
                log.info("Imported " + courseDtos.size() + " courses.");
            } else {
                if (Objects.nonNull(version)) {
                    throw new EducationException(ErrorEnum.CORRUPTED_FILE,
                            ErrorEnum.CORRUPTED_FILE.getExternalMessage());
                }
            }
        }
        return true;
    }
}
