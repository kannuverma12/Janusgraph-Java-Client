package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class EntityDetailsService {

    private CommonMongoRepository commonMongoRepository;

    public <T> T getEntityDetails(String keyName, long entityId, Class<T> type,
            String fieldGroup, String fields) {
        if (StringUtils.isNotBlank(fieldGroup) && StringUtils.isNotBlank(fields)) {
            log.error("Either fields or field_group should be requested.");
            throw new RuntimeException("Bad Request.");
        }

        List<String> queryFields = null;
        if (StringUtils.isNotBlank(fieldGroup)) {
            queryFields = commonMongoRepository.getFieldsByGroup(type, fieldGroup);
        } else if (StringUtils.isNotBlank(fields)) {
            queryFields = Arrays.stream(fields.split(",")).collect(Collectors.toList());
        }

        if (!CollectionUtils.isEmpty(queryFields)) {
            return commonMongoRepository.getEntityByFields(keyName, entityId, type, queryFields);
        }
        return commonMongoRepository.getEntityById(keyName, entityId, type);
    }
}
