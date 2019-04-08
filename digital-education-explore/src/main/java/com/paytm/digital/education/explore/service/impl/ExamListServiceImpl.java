package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.NotFoundException;
import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.SubExam;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.response.dto.detail.ExamInfo;
import com.paytm.digital.education.explore.service.ExamListService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.QueryOperators.OR;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CUTOFF_EXAM_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_FULL_NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_SHORT_NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SUBEXAM_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.NO_EXAM_LIST_EXISTS;

@Slf4j
@AllArgsConstructor
@Service
public class ExamListServiceImpl implements ExamListService {
    private CommonMongoRepository commonMongoRepository;

    @Cacheable(value = "exam_list")
    public List<ExamInfo> getExamList(long instituteId) {
        Map<String, Object> courseQueryFields = new HashMap<>();
        courseQueryFields.put(INSTITUTE_ID, instituteId);
        List<Long> examIds = commonMongoRepository
                .findAllDistinctValues(courseQueryFields, Course.class, CUTOFF_EXAM_ID,
                        Long.class);
        if (!CollectionUtils.isEmpty(examIds)) {
            List<String> examFields = Arrays.asList(EXAM_FULL_NAME, EXAM_SHORT_NAME, EXAM_ID,
                    SUBEXAM_ID);
            Map<String, Object> queryObject = new HashMap<>();
            queryObject.put(SUBEXAM_ID, examIds);
            queryObject.put(EXAM_ID, examIds);
            List<Exam> exams = commonMongoRepository
                    .findAll(queryObject, Exam.class, examFields, OR);
            if (!CollectionUtils.isEmpty(exams)) {
                return buildExamListResponse(exams, examIds);
            }
        }
        throw new NotFoundException(NO_EXAM_LIST_EXISTS,
                NO_EXAM_LIST_EXISTS.getExternalMessage());
    }

    private List<ExamInfo> buildExamListResponse(List<Exam> exams, List<Long> examIds) {
        List<ExamInfo> examLists = new ArrayList<>();
        for (Exam exam : exams) {
            long examId = exam.getExamId();
            if (examIds.contains(examId)) {
                ExamInfo examInfo = getExamInfo(examId, exam.getExamShortName(),
                        exam.getExamFullName());
                examLists.add(examInfo);
            } else {
                for (SubExam subExam : exam.getSubExams()) {
                    long subexamId = subExam.getId();
                    if (examIds.contains(subexamId)) {
                        ExamInfo examInfo = getExamInfo(subexamId, exam.getExamShortName(),
                                exam.getExamFullName());
                        examLists.add(examInfo);
                        break;
                    }
                }
            }

        }
        return examLists;
    }

    private ExamInfo getExamInfo(long examId, String examShortName, String examFullName) {
        ExamInfo examInfo = new ExamInfo();
        examInfo.setExamId(examId);
        if (StringUtils.isBlank(examShortName)) {
            examInfo.setExamName(examFullName);
        } else {
            examInfo.setExamName(examShortName);
        }
        return examInfo;
    }
}
