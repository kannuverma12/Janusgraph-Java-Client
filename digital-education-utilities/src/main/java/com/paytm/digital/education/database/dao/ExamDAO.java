package com.paytm.digital.education.database.dao;

import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.repository.ExamRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExamDAO {

    @Autowired ExamRepository examRepository;

    public Exam findByExamId(@NonNull Long examId) {
        return examRepository.findByExamId(examId);
    }
}
