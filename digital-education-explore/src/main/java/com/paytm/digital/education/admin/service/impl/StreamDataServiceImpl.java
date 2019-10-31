package com.paytm.digital.education.admin.service.impl;

import static com.paytm.digital.education.constant.ExploreConstants.COURSE_ID;
import static com.paytm.digital.education.constant.ExploreConstants.DOMAINS;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.constant.ExploreConstants.STREAMS;
import static com.paytm.digital.education.constant.ExploreConstants.STREAM_IDS;
import static com.paytm.digital.education.ingestion.constant.IngestionConstants.MERCHANT_CAREER_360;
import static com.paytm.digital.education.mapping.ErrorEnum.ENTITY_NOT_SUPPORTED;

import com.paytm.digital.education.database.entity.Course;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.explore.service.helper.StreamDataTranslator;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StreamDataServiceImpl {

    private static Logger log = LoggerFactory.getLogger(StreamDataServiceImpl.class);

    private final CommonMongoRepository commonMongoRepository;
    private final StreamDataTranslator  streamDataTranslator;

    @Value("${explore.entity.paged.size}")
    private Integer documentsPageSize;

    private static List<String> projectionFields =
            Arrays.asList(EXAM_ID, COURSE_ID, DOMAINS, STREAMS, STREAM_IDS);

    public long updatePaytmStream(EducationEntity educationEntity) {
        switch (educationEntity) {
            case EXAM:
                return updatePaytmStreams(Exam.class, 0, documentsPageSize);
            case COURSE:
                return updatePaytmStreams(Course.class, 0, documentsPageSize);
            default:
                throw new EducationException(ENTITY_NOT_SUPPORTED,
                        ENTITY_NOT_SUPPORTED.getExternalMessage());
        }
    }

    private <T> long updatePaytmStreams(Class<T> entityType, int page, int size) {
        Page<T> pagedData = commonMongoRepository.getPagedEntityData(entityType, page, size, projectionFields);
        long updateCount = 0;
        while (Objects.nonNull(pagedData) && pagedData.hasContent()) {
            List<T> entityData = pagedData.getContent();
            for (T singleEntity : entityData) {
                updateCount += updatePaytmStream(entityType, singleEntity);
            }
            pagedData = commonMongoRepository
                    .getPagedEntityData(entityType, pagedData.getNumber() + 1, size, projectionFields);
        }
        return updateCount;
    }

    private <T> long updatePaytmStream(Class<T> type, T entity) {
        if (type == Exam.class) {
            Exam exam = (Exam) type.cast(entity);
            return updateInDB(type, exam.getDomains(), exam.getExamId(), EXAM_ID);
        } else if (type == Course.class) {
            Course course = (Course) type.cast(entity);
            return updateInDB(type, course.getStreams(), course.getCourseId(), COURSE_ID);
        }
        return 0;
    }

    private <T> long updateInDB(Class<T> type, List<String> streams, Long entityId,
            String entityKey) {
        if (!CollectionUtils.isEmpty(streams)) {
            try {
                List<Long> streamIds = streamDataTranslator
                        .getPaytmStreams(streams, MERCHANT_CAREER_360, entityId, type);
                Map<String, Object> updateObject = new HashMap<>();
                updateObject.put(STREAM_IDS, streamIds);
                return commonMongoRepository.updateFields(updateObject, type, entityId, entityKey);
            } catch (Exception ex) {
                log.error(
                        "Error in updating paytm stream for entity : {}, entityId : {}, exception : {}",
                        type.getName(), entityId, ex);
            }

        }
        return 0;
    }
}
