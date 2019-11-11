package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.constant.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.EXAM_FILE_VERSION;
import static com.paytm.digital.education.ingestion.constant.IngestionConstants.MERCHANT_CAREER_360;

import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.explore.database.ingestion.Exam;
import com.paytm.digital.education.explore.service.helper.IncrementalDataHelper;
import com.paytm.digital.education.explore.service.helper.StreamDataTranslator;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TransformAndSaveExamService {

    private static Logger log = LoggerFactory.getLogger(TransformAndSaveExamService.class);

    private IncrementalDataHelper incrementalDataHelper;
    private CommonMongoRepository commonMongoRepository;
    private StreamDataTranslator streamDataTranslator;

    public Integer transformAndSave(List<Exam> exams, Boolean versionUpdate) throws
            EducationException {
        int updatedExams = 0;
        try {
            log.info("Transforming exams.");
            List<Long> examIds =
                    exams.stream().map(e2 -> e2.getExamId()).collect(Collectors.toList());
            Set<Long> examIdSet = new HashSet<>();
            Set<Exam> examSet = new HashSet<>();
            for (Exam exam : exams) {
                if (!examSet.contains(exam.getExamId())) {
                    log.info("Found Exam document from dump for id : {}, {} ", exam.getExamId(),
                            JsonUtils.toJson(exam));
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
            log.info("Saving exams to db.");
            for (Exam exam : examSet) {
                com.paytm.digital.education.database.entity.Exam currentExam =
                        map.get(exam.getExamId());
                if (Objects.nonNull(currentExam)) {
                    String id = currentExam.getId();
                    if (StringUtils.isNotBlank(id)) {
                        exam.setId(id);
                        exam.setStreamIds(exam.getStreamIds());
                        exam.setPriority(exam.getPriority());
                    }
                    //set paytm stream
                    if (!CollectionUtils.isEmpty(exam.getDomains())) {
                        exam.setStreamIds(streamDataTranslator
                                .getPaytmStreams(exam.getDomains(), MERCHANT_CAREER_360,
                                        exam.getExamId(), Exam.class));
                    }
                    exam.setPaytmKeys(currentExam.getPaytmKeys());
                }
                commonMongoRepository.saveOrUpdate(exam);
                updatedExams++;
            }
            log.info("Saved exams to db.");
            if (Objects.isNull(versionUpdate) || versionUpdate == true) {
                log.info("Updating version number for exam");
                incrementalDataHelper.incrementFileVersion(EXAM_FILE_VERSION);
            }
        } catch (Exception e) {
            log.info("Exam ingestion exceptions : ", e);
            if (Objects.nonNull(versionUpdate)) {
                throw new BadRequestException(ErrorEnum.CORRUPTED_FILE,
                        ErrorEnum.CORRUPTED_FILE.getExternalMessage());
            }
        }
        return updatedExams;
    }
}
