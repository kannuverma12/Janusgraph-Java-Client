package com.paytm.digital.education.deal.data.service;

import com.paytm.digital.education.deal.database.entity.DealsEligibleStudentData;
import com.paytm.digital.education.deal.database.repository.StudentDataRepository;
import com.paytm.digital.education.deal.response.dto.ResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StudentDataService {

    private StudentDataRepository studentDataRepository;

    public ResponseDto addStudentData(DealsEligibleStudentData studentData) {
        return studentDataRepository.addStudentData(studentData);
    }
}
