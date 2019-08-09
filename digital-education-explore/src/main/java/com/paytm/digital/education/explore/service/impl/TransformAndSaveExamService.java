package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.database.ingestion.Exam;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.service.helper.IncrementalDataHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.EXAM_FILE_VERSION;

@AllArgsConstructor
@Service
@Slf4j
public class TransformAndSaveExamService {
    private IncrementalDataHelper incrementalDataHelper;
    private CommonMongoRepository commonMongoRepository;

    public void transformAndSave(List<Exam> exams, Boolean versionUpdate) {
        try {
            List<Long> examIds =
                    exams.stream().map(e2 -> e2.getExamId()).collect(Collectors.toList());
            Map<Long, String> map = new HashMap<>();
            if (!examIds.isEmpty()) {
                List<Exam> existingExams =
                        incrementalDataHelper.getExistingData(Exam.class, EXAM_ID,
                                examIds);
                map = existingExams.stream()
                        .collect(Collectors.toMap(c -> c.getExamId(), c -> c.getId()));
            }
            for (Exam exam : exams) {
                String id = map.get(exam.getExamId());
                if (StringUtils.isNotBlank(id)) {
                    exam.setId(id);
                }
                commonMongoRepository.saveOrUpdate(exam);
            }
            if (Objects.isNull(versionUpdate) || versionUpdate == true) {
                incrementalDataHelper.incrementFileVersion(EXAM_FILE_VERSION);
            }
        } catch (Exception e) {
            log.info("Exam ingestion exceptions : " + e.getMessage());
        }
    }
}
