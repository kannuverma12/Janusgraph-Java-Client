package com.paytm.digital.education.coaching.db.dao;

import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.repository.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExamDAO {

    @Autowired ExamRepository examRepository;

    public Exam findByExamId(Long examId) {
        return examRepository.findByExamId(examId);
    }
}
