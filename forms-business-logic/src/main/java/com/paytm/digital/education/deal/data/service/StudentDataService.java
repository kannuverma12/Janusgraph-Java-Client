package com.paytm.digital.education.deal.data.service;

import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_CUSTOMER_ID;

import com.paytm.digital.education.deal.database.entity.DealsStudentData;
import com.paytm.digital.education.deal.database.repository.DealsStudentDataRepository;
import com.paytm.digital.education.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class StudentDataService {

    private DealsStudentDataRepository studentDataRepository;

    public DealsStudentData addStudentData(DealsStudentData studentData) {
        return studentDataRepository.saveOrUpdateStudentData(studentData);
    }

    public DealsStudentData fetchStudentData(Long customerId) {
        DealsStudentData studentData = studentDataRepository.fetchStudentData(customerId);
        if (Objects.isNull(studentData)) {
            throw new BadRequestException(INVALID_CUSTOMER_ID,
                    INVALID_CUSTOMER_ID.getExternalMessage());
        }
        return studentData;
    }
}
