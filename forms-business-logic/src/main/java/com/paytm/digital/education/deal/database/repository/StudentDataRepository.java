package com.paytm.digital.education.deal.database.repository;

import com.paytm.digital.education.deal.database.entity.DealsEligibleStudentData;
import com.paytm.digital.education.deal.enums.StatusType;
import com.paytm.digital.education.deal.response.dto.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import static com.paytm.digital.education.utility.DateUtil.getCurrentDate;

@Slf4j
@Repository
@AllArgsConstructor
public class StudentDataRepository {
    private MongoOperations mongoOperations;

    public ResponseDto addStudentData(DealsEligibleStudentData studentData) {
        studentData.setCreatedAt(getCurrentDate());
        studentData.setUpdatedAt(studentData.getCreatedAt());
        studentData.setMobileVerified(false);
        studentData.setEmailVerified(false);
        studentData.setStatus(StatusType.PENDING);
        mongoOperations.save(studentData);
        return studentData;
    }
}
