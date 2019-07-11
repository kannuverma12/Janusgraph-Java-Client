package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.ingestion.Course;
import com.paytm.digital.education.explore.database.ingestion.Exam;
import com.paytm.digital.education.explore.dto.InstituteDto;
import com.paytm.digital.education.explore.service.helper.IncrementalDataHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.COURSES_FILE_NAME;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.EXAM_FILE_NAME;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.INSTITUTE_FILE_NAME;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.INSTITUTE_FILE_VERSION;

@AllArgsConstructor
@Service
@Slf4j
public class ImportIncrementalDataService {
    private IncrementalDataHelper         incrementalDataHelper;
    private TransformAndSaveCourseService transformAndSaveCourseService;
    private TransformAndSaveExamService   transformAndSaveExamService;
    private TransformInstituteService     transformInstituteService;

    public boolean importData() {
        Map<String, Boolean> fileInfo = incrementalDataHelper.downloadFileFromSftp();
        if (fileInfo.get(INSTITUTE_FILE_NAME)) {
            List<InstituteDto> instituteDtos =
                    incrementalDataHelper.retrieveDataFromFile(INSTITUTE_FILE_NAME,
                            InstituteDto.class);
            List<Institute> institutes =
                    transformInstituteService.transformInstituteDtos(instituteDtos);
            // Karan need to update as per his convenience
            incrementalDataHelper.incrementFileVersion(INSTITUTE_FILE_VERSION);
        }
        if (fileInfo.get(EXAM_FILE_NAME)) {
            List<Exam> examDtos = incrementalDataHelper.retrieveDataFromFile(EXAM_FILE_NAME,
                    Exam.class);
            if (!examDtos.isEmpty()) {
                transformAndSaveExamService.transformAndSave(examDtos);
            }
        }
        if (fileInfo.get(COURSES_FILE_NAME)) {
            List<Course> courseDtos =
                    incrementalDataHelper.retrieveDataFromFile(COURSES_FILE_NAME, Course.class);
            if (!courseDtos.isEmpty()) {
                transformAndSaveCourseService.transformAndSave(courseDtos);
            }
        }
        return true;
    }
}
