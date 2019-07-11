package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.dto.CourseDto;
import com.paytm.digital.education.explore.dto.ExamDto;
import com.paytm.digital.education.explore.service.helper.IncrementalDataHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;
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

    public boolean importData() throws FileNotFoundException {
        //Map<String, Boolean> fileInfo = incrementalDataHelper.downloadFileFromSftp();
        Map<String, Boolean> fileInfo = new HashMap<>();
        fileInfo.put(EXAM_FILE_NAME, false);
        fileInfo.put(INSTITUTE_FILE_NAME, false);
        fileInfo.put(COURSES_FILE_NAME, true);
        if (fileInfo.get(INSTITUTE_FILE_NAME)) {
            // Insert institute data
        }
        if (fileInfo.get(EXAM_FILE_NAME)) {
            List<ExamDto> examDtos = incrementalDataHelper.retrieveDataFromFile(EXAM_FILE_NAME,
                    ExamDto.class);
            if (!examDtos.isEmpty()) {
                transformAndSaveExamService.transformAndSave(examDtos);
            }
        }
        if (fileInfo.get(COURSES_FILE_NAME)) {
            List<CourseDto> courseDtos =
                    incrementalDataHelper.retrieveDataFromFile(COURSES_FILE_NAME, CourseDto.class);
            if (!courseDtos.isEmpty()) {
                transformAndSaveCourseService.transformAndSave(courseDtos);
            }
        }
        return true;
    }
}
