package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.service.EntityDetailsService;
import lombok.AllArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


@AllArgsConstructor
@Service
public class EntityDetailsServiceImpl implements EntityDetailsService {

    private CommonMongoRepository commonMongoRepository;

    @Override
    @Cacheable(value = "entity_detail", unless = "#result == null")
    public <T> T getEntityDetails(String keyName, long entityId, Class<T> type,
            String fieldGroup, List<String> fields) {
        List<String> queryFields = null;
        if (StringUtils.isNotBlank(fieldGroup)) {
            queryFields = commonMongoRepository.getFieldsByGroup(type, fieldGroup);
        } else if (!CollectionUtils.isEmpty(fields)) {
            queryFields = fields;
        }

        if (!CollectionUtils.isEmpty(queryFields)) {
            return commonMongoRepository.getEntityByFields(keyName, entityId, type, queryFields);
        }
        return commonMongoRepository.getEntityById(keyName, entityId, type);
    }
}
