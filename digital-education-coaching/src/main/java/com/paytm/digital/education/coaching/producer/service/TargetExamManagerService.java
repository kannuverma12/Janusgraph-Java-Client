package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.model.dto.TargetExamDTO;
import com.paytm.digital.education.coaching.producer.model.request.TargetExamUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TargetExamManagerService {

    @Autowired
    private ProducerTargetExamService producerTargetExamService;

    @Autowired
    private ProducerStreamService producerStreamService;

    public TargetExamDTO update(TargetExamUpdateRequest request) {

        producerStreamService.isValidStreamIds(request.getStreamIds());

        return TargetExamDTO.builder().examId(producerTargetExamService.update(request).getExamId())
                .build();
    }
}
