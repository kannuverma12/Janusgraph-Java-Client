package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.model.dto.TargetExamDTO;
import com.paytm.digital.education.coaching.producer.model.request.TargetExamUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TargetExamManagerService {

    @Autowired
    private TargetExamService targetExamService;

    @Autowired
    private StreamService streamService;

    public TargetExamDTO update(TargetExamUpdateRequest request) {

        streamService.isValidStreamIds(request.getStreamIds());

        return TargetExamDTO.builder().examId(targetExamService.update(request).getExamId())
                .build();
    }
}
