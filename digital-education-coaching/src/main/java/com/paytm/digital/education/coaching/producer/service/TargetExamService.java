package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.db.dao.TargetExamDAO;
import com.paytm.digital.education.coaching.producer.ConverterUtil;
import com.paytm.digital.education.coaching.producer.model.request.TargetExamUpdateRequest;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TargetExamService {

    @Autowired
    private TargetExamDAO targetExamDAO;

    public Exam update(TargetExamUpdateRequest request) {

        Exam existingExam =
                Optional.ofNullable(targetExamDAO.findByExamId(request.getExamId()))
                        .orElseThrow(() -> new InvalidRequestException(
                                "exam id not present : " + request.getExamId()));
        ConverterUtil.setExamUpdateData(request, existingExam);
        try {
            return targetExamDAO.save(existingExam);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }
    }

    public boolean isValidExamIds(List<Long> ids) {
        List<Long> existingExamIds = targetExamDAO.findAllByExamId(ids)
                .stream().map(Exam::getExamId).collect(Collectors.toList());
        List<Long> invalidExamIds = ids.stream().filter(id -> !existingExamIds.contains(id))
                .collect(Collectors.toList());
        if (!invalidExamIds.isEmpty()) {
            throw new InvalidRequestException("Invalid exam ids given : " + invalidExamIds);
        }
        return true;
    }
}
