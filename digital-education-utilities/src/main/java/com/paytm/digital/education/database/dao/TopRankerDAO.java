package com.paytm.digital.education.database.dao;

import com.paytm.digital.education.database.entity.TopRankerEntity;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.database.repository.SequenceGenerator;
import com.paytm.digital.education.database.repository.TopRankerRepository;
import com.paytm.digital.education.metrics.annotations.NullValueAlert;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.mongodb.QueryOperators.AND;

@Component
public class TopRankerDAO {

    @Autowired
    TopRankerRepository topRankerRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private CommonMongoRepository commonMongoRepository;

    public TopRankerEntity save(@NonNull TopRankerEntity topRankerEntity) {
        if (Objects.isNull(topRankerEntity.getTopRankerId())) {
            topRankerEntity.setTopRankerId(sequenceGenerator
                    .getNextSequenceId(topRankerEntity.getClass().getSimpleName()));
        }
        return topRankerRepository.save(topRankerEntity);
    }

    public TopRankerEntity findByTopRankerId(@NonNull Long id) {
        return topRankerRepository.findByTopRankerId(id);
    }

    public List<TopRankerEntity> findAll() {
        return this.topRankerRepository.findAll();
    }

    @NullValueAlert(mandatoryFields = "#mandatoryFields")
    public List<TopRankerEntity> findByCourseIdsInAndSortBy(String courseIdsField,
            List<Long> courseIds,
            List<String> projectionFields, Map<Sort.Direction, String> sortMap,
            List<String> mandatoryFields) {
        return commonMongoRepository
                .getEntityFieldsByValuesInAndSortBy(courseIdsField,
                        courseIds, TopRankerEntity.class,
                        projectionFields, sortMap);
    }

    @NullValueAlert(mandatoryFields = "#mandatoryFields")
    public List<TopRankerEntity> findByInstituteIdsInAndSortBy(String instituteIdField,
            List<Long> instituteIds,
            List<String> projectionFields, Map<Sort.Direction, String> sortMap,
            List<String> mandatoryFields) {
        return commonMongoRepository
                .getEntityFieldsByValuesInAndSortBy(instituteIdField,
                        instituteIds, TopRankerEntity.class,
                        projectionFields, sortMap);
    }

    @NullValueAlert(mandatoryFields = "#mandatoryFields")
    public List<TopRankerEntity> findByInstituteIdAndIsEnabledAndExamIdAndSortBy(
            String instituteIdField, long instituteId, String isEnabledField, Boolean isEnabled,
            String examIdField, long examId, List<String> projectionFields,
            Map<Sort.Direction, String> sortMap, int limit, List<String> mandatoryFields) {
        final Map<String, Object> searchRequest = new HashMap<>();
        searchRequest.put(instituteIdField, instituteId);
        searchRequest.put(isEnabledField, isEnabled);
        searchRequest.put(examIdField, examId);
        return commonMongoRepository.findAllAndSortBy(
                searchRequest, TopRankerEntity.class, projectionFields, AND,
                sortMap, limit);
    }

    @NullValueAlert(mandatoryFields = "#mandatoryFields")
    public List<TopRankerEntity> findByInstituteIdAndIsEnabledAndStreamIdsAndSortBy(
            String instituteIdField, long instituteId, String isEnabledField, Boolean isEnabled,
            String streamIdsField, long streamId, List<String> projectionFields,
            Map<Sort.Direction, String> sortMap, int limit, List<String> mandatoryFields) {
        final Map<String, Object> searchRequest = new HashMap<>();
        searchRequest.put(instituteIdField, instituteId);
        searchRequest.put(isEnabledField, isEnabled);
        searchRequest.put(streamIdsField, streamId);
        return commonMongoRepository.findAllAndSortBy(
                searchRequest, TopRankerEntity.class, projectionFields, AND,
                sortMap, limit);
    }

    @NullValueAlert(mandatoryFields = "#mandatoryFields")
    public List<TopRankerEntity> findByInstituteIdAndIsEnabledAndSortBy(
            String instituteIdField, long instituteId, String isEnabledField, Boolean isEnabled,
            List<String> projectionFields, Map<Sort.Direction, String> sortMap, int limit,
            List<String> mandatoryFields) {
        final Map<String, Object> searchRequest = new HashMap<>();
        searchRequest.put(instituteIdField, instituteId);
        searchRequest.put(isEnabledField, isEnabled);
        return commonMongoRepository.findAllAndSortBy(
                searchRequest, TopRankerEntity.class, projectionFields, AND,
                sortMap, limit);
    }

}
