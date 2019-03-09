package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.service.CommonMongoService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CommonMongoServiceImpl implements CommonMongoService {
    private CommonMongoRepository commonMongoRepository;

    @Override
    @Cacheable(value = "fieldGroups")
    public List<String> getFieldsByGroupAndCollectioName(String collectionName, String fieldGroup) {
        return commonMongoRepository.getFieldsByGroupAndCollectioName(collectionName, fieldGroup);
    }
}
