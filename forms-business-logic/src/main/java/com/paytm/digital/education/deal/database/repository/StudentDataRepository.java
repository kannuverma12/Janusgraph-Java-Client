package com.paytm.digital.education.deal.database.repository;

import com.paytm.digital.education.deal.database.entity.DealsEligibleStudentData;
import com.paytm.digital.education.deal.enums.StatusType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import static com.paytm.digital.education.deal.constants.DealConstant.CUSTOMER_ID;
import static com.paytm.digital.education.utility.DateUtil.getCurrentDate;

@Slf4j
@Repository
@AllArgsConstructor
public class StudentDataRepository {
    private MongoOperations mongoOperations;

    public DealsEligibleStudentData addStudentData(DealsEligibleStudentData studentData) {
        studentData.setCreatedAt(getCurrentDate());
        studentData.setUpdatedAt(studentData.getCreatedAt());
        studentData.setMobileVerified(false);
        studentData.setEmailVerified(false);
        studentData.setStatus(StatusType.PENDING);
        mongoOperations.save(studentData);
        return studentData;
    }

    public DealsEligibleStudentData fetchStudentData(Long customerId) {
        Criteria criteria = Criteria.where(CUSTOMER_ID).is(customerId);
        Query mongoQuery = new Query(criteria);
        DealsEligibleStudentData studentData =
                mongoOperations.findOne(mongoQuery, DealsEligibleStudentData.class);
        return studentData;
    }
}
