package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.repository.StreamRepository;
import com.paytm.digital.education.explore.service.StreamService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StreamServiceImpl implements StreamService {

    private StreamRepository streamRepository;

    @Cacheable(value = "streams", key = "streams", unless = "#result == null")
    public Iterable<StreamEntity> getAllStreams() {
        return streamRepository.findAll();
    }
}
