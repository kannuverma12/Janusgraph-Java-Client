package com.paytm.digital.education.ingestion.service;

import static com.paytm.digital.education.ingestion.constant.IngestionConstants.MERCHANT_CAREER_360;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_STREAM_MERCHANT_STREAM_MAPPING;
import static com.paytm.digital.education.mapping.ErrorEnum.NO_SUCH_MERCHANT_STREAM;
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
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.exception.InvalidRequestException;
import com.paytm.digital.education.ingestion.sheets.ExamStreamForm;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        if (null == examStreamForm.getExamId() || examStreamForm.getExamId() <= 0
                || null == examRepository.findByExamId(examStreamForm.getExamId())) {
            throw new BadRequestException(INVALID_EXAM_ID, INVALID_EXAM_ID.getExternalMessage());
        }

        Exam exam = Optional.ofNullable(examRepository.findByExamId(examStreamForm.getExamId()))
                .orElseThrow(() -> new InvalidRequestException(
                        "invalid exam id : " + examStreamForm.getExamId()));

        if (StringUtils.isBlank(examStreamForm.getMerchantStream()) || StringUtils
                .isBlank(examStreamForm.getPaytmStream())) {
            throw new EducationException(INVALID_STREAM_MERCHANT_STREAM_MAPPING,
                    INVALID_STREAM_MERCHANT_STREAM_MAPPING.getExternalMessage(),
                    new Object[] {examStreamForm.getPaytmStream(),
                            examStreamForm.getMerchantStream()});
        }

        Map<String, StreamEntity> streamEntityMap = getPaytmStreamMap();
        if (!streamEntityMap.containsKey(examStreamForm.getPaytmStream())) {
            new EducationException(NO_SUCH_PAYTM_STREAM, NO_SUCH_PAYTM_STREAM.getExternalMessage(),
                    new Object[] {examStreamForm.getPaytmStream()});
        }

        Map<String, MerchantStreamEntity> merchantStreamEntityMap = getMerchantStreamMap(MERCHANT_CAREER_360);
        if (!merchantStreamEntityMap.containsKey(examStreamForm.getMerchantStream())) {
            new EducationException(NO_SUCH_MERCHANT_STREAM,
                    NO_SUCH_MERCHANT_STREAM.getExternalMessage(),
                    new Object[] {examStreamForm.getMerchantStream(), MERCHANT_CAREER_360});
        }

        ExamStreamEntity examStreamEntity = Optional.ofNullable(examStreamMappingRepository
                .findByExamIdAndAndMerchantStreamAndPaytmStream(examStreamForm.getExamId(),
                        examStreamForm.getMerchantStream(), examStreamForm.getPaytmStream()))
                .orElse(new ExamStreamEntity());

        return examStreamMappingRepository
                .save(convertToExamStreamEntity(examStreamForm, examStreamEntity,
                        streamEntityMap.get(examStreamForm.getPaytmStream())));
    }

    @Cacheable(value = "merchant_streams")
    public Map<String, MerchantStreamEntity> getMerchantStreamMap(String merchant) {
        List<MerchantStreamEntity> merchantStreamEntities = merchantStreamRepository.findAllByMerchantId(merchant);
        return Optional.ofNullable(merchantStreamEntities).map(streams -> streams.stream()
                .collect(Collectors.toMap(s -> s.getStream(), s -> s)))
                .orElse(new HashMap<>());
    }

    @Cacheable(value = "paytm_streams")
    public Map<String, StreamEntity> getPaytmStreamMap() {
        List<StreamEntity> streamEntities = streamEntityRepository.findAll();
        return Optional.ofNullable(streamEntities).map(streams -> streams.stream()
                .collect(Collectors.toMap(s -> s.getName(), s -> s))).orElse(new HashMap<>());
    }

    private ExamStreamEntity convertToExamStreamEntity(ExamStreamForm examStreamForm,
            ExamStreamEntity examStreamEntity,
            StreamEntity streamEntity) {
        examStreamEntity.setExamId(examStreamForm.getExamId());
        examStreamEntity.setExamFullName(examStreamForm.getExamFullName());
        examStreamEntity.setExamShortName(examStreamForm.getExamShortName());
        examStreamEntity.setMerchantStream(examStreamForm.getMerchantStream());
        examStreamEntity.setPaytmStream(examStreamForm.getPaytmStream());
        examStreamEntity.setMerchantStream(examStreamForm.getMerchantStream());
        examStreamEntity.setPaytmStreamId(streamEntity.getStreamId());
        examStreamEntity.setPriority(examStreamForm.getGlobalPriority());
        examStreamEntity.setUpdatedAt(LocalDateTime.now());
        examStreamEntity.setIsEnabled(stringToBoolean(examStreamForm.getStatusActive()));
        return examStreamEntity;
    }


}
