package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.NotFoundException;
import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.response.dto.detail.ExamInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAMS_ACCEPTED;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_FULL_NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_SHORT_NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.NO_EXAM_LIST_EXISTS;

@Slf4j
@AllArgsConstructor
@Service
public class ExamListServiceImpl {
    private CommonMongoRepository commonMongoRepository;

    @Cacheable(value = "exam_list")
    public List<ExamInfo> getExamList(long instituteId) {
        List<String> courseFields = Arrays.asList(EXAMS_ACCEPTED);
        List<Course> courses = commonMongoRepository
                .getEntitiesByIdAndFields(INSTITUTE_ID, instituteId, Course.class,
                        courseFields);
        Set<Long> examIds = new HashSet<>();
        if (!CollectionUtils.isEmpty(courses)) {
            courses.forEach(course -> {
                if (!CollectionUtils.isEmpty(course.getExamsAccepted())) {
                    course.getExamsAccepted().forEach(examId -> {
                        examIds.add(examId);
                    });
                }
            });
            if (!CollectionUtils.isEmpty(examIds)) {
                List<String> examFields = Arrays.asList(EXAM_FULL_NAME, EXAM_SHORT_NAME, EXAM_ID);
                List<Exam> exams = commonMongoRepository
                        .getEntityFieldsByValuesIn(EXAM_ID, new ArrayList<>(examIds),
                                com.paytm.digital.education.explore.database.entity.Exam.class,
                                examFields);
                List<ExamInfo> examLists = new ArrayList<>();
                for (com.paytm.digital.education.explore.database.entity.Exam exam : exams) {
                    ExamInfo examInfo = new ExamInfo();
                    examInfo.setExamId(exam.getExamId());
                    if (exam.getExamShortName() == null) {
                        examInfo.setExamName(exam.getExamFullName());
                    } else {
                        examInfo.setExamName(exam.getExamShortName());
                    }
                    examLists.add(examInfo);
                }
                return examLists;
            }
        }
        throw new NotFoundException(NO_EXAM_LIST_EXISTS,
                NO_EXAM_LIST_EXISTS.getExternalMessage());
    }
}
