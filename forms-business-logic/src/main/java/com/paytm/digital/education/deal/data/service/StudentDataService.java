package com.paytm.digital.education.deal.data.service;

import com.paytm.digital.education.deal.database.entity.DealsEligibleStudentData;
import com.paytm.digital.education.deal.database.repository.StudentDataRepository;
import com.paytm.digital.education.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_CUSTOMER_ID;

@Service
@AllArgsConstructor
public class StudentDataService {

    private StudentDataRepository studentDataRepository;

    public DealsEligibleStudentData addStudentData(DealsEligibleStudentData studentData) {
        return studentDataRepository.addStudentData(studentData);
    }

    public DealsEligibleStudentData fetchStudentData(Long customerId) {
        DealsEligibleStudentData studentData = studentDataRepository.fetchStudentData(customerId);
        if (Objects.isNull(studentData)) {
            throw new BadRequestException(INVALID_CUSTOMER_ID,
                    INVALID_CUSTOMER_ID.getExternalMessage());
        }
        return studentData;
    }
}
