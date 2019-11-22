package com.paytm.digital.education.ingestion.service;

import static com.paytm.digital.education.mapping.ErrorEnum.NO_SUCH_PAYTM_STREAM;
import static com.paytm.digital.education.utility.CommonUtils.stringToBoolean;

import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.ExamStreamEntity;
import com.paytm.digital.education.database.entity.MerchantStreamEntity;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.repository.ExamRepository;
import com.paytm.digital.education.database.repository.ExamStreamMappingRepository;
import com.paytm.digital.education.database.repository.MerchantStreamRepository;
import com.paytm.digital.education.database.repository.StreamEntityRepository;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.exception.InvalidRequestException;
import com.paytm.digital.education.ingestion.sheets.ExamStreamForm;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

@Service
public class ExamStreamManagerService {

    private static Logger log = LoggerFactory.getLogger(ExamStreamManagerService.class);

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private StreamEntityRepository streamEntityRepository;

    @Autowired
    private MerchantStreamRepository merchantStreamRepository;

    @Autowired
    private ExamStreamMappingRepository examStreamMappingRepository;

    public ExamStreamEntity createOrUpdateExamStreamMapping(
            @NotNull ExamStreamForm examStreamForm) {
        Exam exam = Optional.ofNullable(examRepository.findByExamId(examStreamForm.getExamId()))
                .orElseThrow(() -> new InvalidRequestException(
                        "invalid exam id : " + examStreamForm.getExamId()));

        Map<Long, StreamEntity> streamEntityMap = getPaytmStreamMap();
        List<Long> paytmStreamIds = Arrays.stream(examStreamForm.getDomains().split(","))
                .map(str -> Long.parseLong(str.trim())).collect(Collectors.toList());

        for (Long paytmStreamId : paytmStreamIds) {
            if (!streamEntityMap.containsKey(paytmStreamId)) {
                log.error("Paytm Stream Id : {}, doesn't exist in our system.", paytmStreamId);
                throw new EducationException(NO_SUCH_PAYTM_STREAM, NO_SUCH_PAYTM_STREAM.getExternalMessage(),
                        new Object[] {paytmStreamId});
            }
        }

        ExamStreamEntity examStreamEntity = Optional.ofNullable(examStreamMappingRepository
                .findByExamId(examStreamForm.getExamId()))
                .orElse(new ExamStreamEntity());

        return examStreamMappingRepository
                .save(convertToExamStreamEntity(examStreamForm, examStreamEntity,
                        paytmStreamIds));
    }

    @Cacheable(value = "merchant_streams")
    public Map<String, MerchantStreamEntity> getMerchantStreamMap(String merchant) {
        List<MerchantStreamEntity> merchantStreamEntities = merchantStreamRepository.findAllByMerchantId(merchant);
        return Optional.ofNullable(merchantStreamEntities).map(streams -> streams.stream()
                .collect(Collectors.toMap(s -> s.getStream(), s -> s)))
                .orElse(new HashMap<>());
    }

    @Cacheable(value = "paytm_streams")
    public Map<Long, StreamEntity> getPaytmStreamMap() {
        List<StreamEntity> streamEntities = streamEntityRepository.findAll();
        return Optional.ofNullable(streamEntities).map(streams -> streams.stream()
                .collect(Collectors.toMap(s -> s.getStreamId(), s -> s))).orElse(new HashMap<>());
    }

    private ExamStreamEntity convertToExamStreamEntity(ExamStreamForm examStreamForm,
            ExamStreamEntity examStreamEntity,
            List<Long> paytmStreamIds) {
        examStreamEntity.setExamId(examStreamForm.getExamId());
        examStreamEntity.setExamFullName(examStreamForm.getExamFullName());
        examStreamEntity.setExamShortName(examStreamForm.getExamShortName());
        examStreamEntity.setPaytmStreamIds(paytmStreamIds);
        examStreamEntity.setPriority(examStreamForm.getGlobalPriority());
        examStreamEntity.setUpdatedAt(LocalDateTime.now());
        examStreamEntity.setIsEnabled(stringToBoolean(examStreamForm.getStatusActive()));
        return examStreamEntity;
    }


}
