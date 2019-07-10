package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.dto.CourseDto;
import com.paytm.digital.education.explore.dto.ExamDto;
import com.paytm.digital.education.explore.service.helper.IncrementalDataHelper;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.COURSES_FILE_NAME;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.EXAM_FILE_NAME;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.INSTITUTE_FILE_NAME;

@AllArgsConstructor
@Service
@Slf4j
public class ImportIncrementalDataService {
    private IncrementalDataHelper incrementalDataHelper;
    private CommonMongoRepository commonMongoRepository;

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
            File initialFile = new File(EXAM_FILE_NAME);
            InputStream targetStream = new FileInputStream(initialFile);
            List<ExamDto> exams = incrementalDataHelper.parseAsStream(targetStream, ExamDto.class);
            if (!exams.isEmpty()) {
                Map<String, Object> queryObject = new HashMap<>();
                for (ExamDto exam : exams) {
                    Exam dbExam = new Exam();
                    BeanUtils.copyProperties(exam, dbExam);
                    queryObject.put(EXAM_ID, exam.getExamId());
                    Exam d = commonMongoRepository.getEntityById(EXAM_ID, exam.getExamId(),
                            Exam.class);
                    if (d != null) {
                        dbExam.setId(d.getId());
                    }
                    commonMongoRepository.saveOrUpdate(dbExam);
                }
            }
        }
        if (fileInfo.get(COURSES_FILE_NAME)) {
            // Insert course data
            File initialFile = new File(COURSES_FILE_NAME);
            InputStream targetStream = new FileInputStream(initialFile);
            List<CourseDto> exams = incrementalDataHelper.parseAsStream(targetStream, CourseDto.class);
            if (!exams.isEmpty()) {
                Map<String, Object> queryObject = new HashMap<>();
                for (CourseDto exam : exams) {
                    Course dbExam = new Course();
                    BeanUtils.copyProperties(exam, dbExam);
                    queryObject.put(COURSE_ID, exam.getCourseId());
                    Course d = commonMongoRepository.getEntityById(COURSE_ID, exam.getCourseId(),
                            Course.class);
                    if (d != null) {
                        dbExam.setId(d.getId());
                    }
                    commonMongoRepository.saveOrUpdate(dbExam);
                }
            }
        }
        return true;
    }
}
