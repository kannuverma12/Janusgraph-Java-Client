package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.dto.ExamDto;
import com.paytm.digital.education.explore.service.helper.IncrementalDataHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;

@AllArgsConstructor
@Service
@Slf4j
public class TransformAndSaveExamService {
    private IncrementalDataHelper incrementalDataHelper;
    private CommonMongoRepository commonMongoRepository;

    public void transformAndSave(List<ExamDto> examDtos) {
        List<Long> examIds = new ArrayList<>();
        List<Exam> exams = examDtos.stream()
                .map(e2 -> {
                    Exam exam = new Exam();
                    BeanUtils.copyProperties(e2, exam);
                    return exam;
                })
                .peek(exam -> examIds
                        .add(exam.getExamId()))
                .collect(Collectors.toList());
        Map<Long, String> map = new HashMap<>();
        if (!examIds.isEmpty()) {
            List<Exam> existingCourse =
                    incrementalDataHelper.getExistingData(Exam.class, EXAM_ID,
                            examIds);
            map = existingCourse.stream()
                    .collect(Collectors.toMap(c -> c.getExamId(), c -> c.getId()));
        }
        for (Exam exam : exams) {
            String id = map.get(exam.getExamId());
            if (StringUtils.isNotBlank(id)) {
                exam.setId(id);
            }
            commonMongoRepository.saveOrUpdate(exam);
        }
    }
}
