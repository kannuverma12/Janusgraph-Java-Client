package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.database.ingestion.Exam;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.service.helper.IncrementalDataHelper;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.EXAM_FILE_VERSION;

@AllArgsConstructor
@Service

public class TransformAndSaveExamService {

    private static Logger log = LoggerFactory.getLogger(TransformAndSaveExamService.class);

    private IncrementalDataHelper incrementalDataHelper;
    private CommonMongoRepository commonMongoRepository;

    public void transformAndSave(List<Exam> exams, Boolean versionUpdate) {
        try {
            List<Long> examIds =
                    exams.stream().map(e2 -> e2.getExamId()).collect(Collectors.toList());
            Set<Long> examIdSet = new HashSet<>();
            Set<Exam> examSet = new HashSet<>();
            for (Exam exam : exams) {
                if (!examSet.contains(exam.getExamId())) {
                    examIdSet.add(exam.getExamId());
                    examSet.add(exam);
                }
            }

            Map<Long, com.paytm.digital.education.database.entity.Exam> map =
                    new HashMap<>();
            if (!examIds.isEmpty()) {
                List<com.paytm.digital.education.database.entity.Exam> existingExams =
                        incrementalDataHelper.getExistingData(
                                com.paytm.digital.education.database.entity.Exam.class,
                                EXAM_ID,
                                new ArrayList<>(examIdSet));
                map = existingExams.stream()
                        .collect(Collectors.toMap(c -> c.getExamId(), c -> c));
            }
            for (Exam exam : examSet) {
                com.paytm.digital.education.database.entity.Exam currentExam =
                        map.get(exam.getExamId());
                if (Objects.nonNull(currentExam)) {
                    String id = currentExam.getId();
                    if (StringUtils.isNotBlank(id)) {
                        exam.setId(id);
                    }
                    exam.setPaytmKeys(currentExam.getPaytmKeys());
                }
                commonMongoRepository.saveOrUpdate(exam);
            }
            if (Objects.isNull(versionUpdate) || versionUpdate == true) {
                incrementalDataHelper.incrementFileVersion(EXAM_FILE_VERSION);
            }
        } catch (Exception e) {
            log.info("Exam ingestion exceptions : " + e.getMessage());
            if (Objects.nonNull(versionUpdate)) {
                throw new BadRequestException(ErrorEnum.CORRUPTED_FILE,
                        ErrorEnum.CORRUPTED_FILE.getExternalMessage());
            }
        }
    }
}
