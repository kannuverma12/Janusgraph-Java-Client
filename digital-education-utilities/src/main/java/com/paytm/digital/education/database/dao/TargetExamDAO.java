package com.paytm.digital.education.database.dao;

import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.repository.ExamRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TargetExamDAO {

    @Autowired
    ExamRepository examRepository;

    public Exam save(@NonNull Exam exam) {
        return examRepository.save(exam);
    }

    public Exam findByExamId(@NonNull Long id) {
        return examRepository.findByExamId(id);
    }

    public List<Exam> findAllByExamId(@NonNull List<Long> ids) {
        return examRepository.findAllByExamId(ids);
    }
}
