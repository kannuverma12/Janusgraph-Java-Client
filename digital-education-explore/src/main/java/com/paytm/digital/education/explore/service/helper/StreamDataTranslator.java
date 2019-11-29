package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.constant.ExploreConstants.DEFAULT_EXAM_PRIORITY;
import static com.paytm.digital.education.constant.ExploreConstants.PRIORITY;
import static com.paytm.digital.education.constant.ExploreConstants.STREAM_IDS;
import static com.paytm.digital.education.ingestion.constant.IngestionConstants.MERCHANT_CAREER_360;
import static com.paytm.digital.education.mapping.ErrorEnum.NO_SUCH_MERCHANT_STREAM;

import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.ExamStreamEntity;
import com.paytm.digital.education.database.entity.MerchantStreamEntity;
import com.paytm.digital.education.database.repository.ExamStreamMappingRepository;
import com.paytm.digital.education.database.repository.MerchantStreamRepository;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class StreamDataTranslator {

    private static Logger log = LoggerFactory.getLogger(StreamDataTranslator.class);

    @Autowired
    private MerchantStreamRepository merchantStreamRepository;

    @Autowired
    private ExamStreamMappingRepository examStreamMappingRepository;

    public <T> Map<String, Object> getPaytmStreams(List<String> merchantStreams, String merchantId,
            Long entityId, Class<T> type) {
        Map<String, Object> toReturn = new HashMap<>();
        if (type == Exam.class) {
            toReturn.put(PRIORITY, DEFAULT_EXAM_PRIORITY);
            ExamStreamEntity examStreamEntity =
                    examStreamMappingRepository.findByExamIdAndIsEnabled(entityId, true);
            if (Objects.nonNull(examStreamEntity)) {
                if (Objects.nonNull(examStreamEntity.getPriority()) && examStreamEntity.getPriority() > 0) {
                    toReturn.put(PRIORITY, examStreamEntity.getPriority());
                }
                toReturn.put(STREAM_IDS, examStreamEntity.getPaytmStreamIds());
                return toReturn;
            }
        }

        if (!CollectionUtils.isEmpty(merchantStreams)) {
            List<MerchantStreamEntity> merchantStreamEntities = merchantStreamRepository
                    .findAllByMerchantIdAndStreamIn(merchantId, merchantStreams);
            Map<String, Long> streamToIdMap = Optional.ofNullable(merchantStreamEntities)
                    .map(mStreams -> mStreams.stream().collect(Collectors
                            .toMap(MerchantStreamEntity::getStream,
                                    MerchantStreamEntity::getPaytmStreamId)))
                    .orElse(new HashMap<>());
            for (String merchantStream : merchantStreams) {
                if (!streamToIdMap.containsKey(merchantStream)) {
                    log.error("Merchant stream : {} not mapped with paytm stream in the database. "
                                    + "entity : {}, entityId : {}",
                            merchantStream, type.getName(), entityId);
                    throw new EducationException(NO_SUCH_MERCHANT_STREAM,
                            NO_SUCH_MERCHANT_STREAM.getExternalMessage(),
                            new Object[] {merchantStream, MERCHANT_CAREER_360});
                }
            }
            Set<Long> paytmStreamIds = streamToIdMap.values().stream().collect(Collectors.toSet());
            toReturn.put(STREAM_IDS, paytmStreamIds);
            return toReturn;
        }
        return null;
    }

}
