package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.database.entity.Stream;
import com.paytm.digital.education.explore.database.repository.StreamRepository;
import com.paytm.digital.education.explore.service.StreamService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StreamServiceImpl implements StreamService {

    private StreamRepository streamRepository;

    public Iterable<Stream> getAllStreams() {
        return streamRepository.findAll();
    }
}
