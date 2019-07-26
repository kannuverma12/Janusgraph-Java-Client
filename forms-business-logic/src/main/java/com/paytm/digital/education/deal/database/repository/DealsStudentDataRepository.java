package com.paytm.digital.education.deal.database.repository;

import static com.paytm.digital.education.deal.constants.DealConstant.CUSTOMER_ID;
import static com.paytm.digital.education.deal.constants.DealConstant.REF_ID;
import static com.paytm.digital.education.utility.DateUtil.getCurrentDate;

import com.paytm.digital.education.deal.database.entity.DealsStudentData;
import com.paytm.digital.education.deal.enums.StudentStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@AllArgsConstructor
public class DealsStudentDataRepository {
    private MongoOperations mongoOperations;

    public DealsStudentData saveOrUpdateStudentData(DealsStudentData studentData) {
        studentData.setCreatedAt(getCurrentDate());
        studentData.setUpdatedAt(studentData.getCreatedAt());
        studentData.setMobileVerified(false);
        studentData.setEmailVerified(false);
        studentData.setStatus(StudentStatus.REGISTERED);
        DealsStudentData dbStudentData = mongoOperations.save(studentData);
        return dbStudentData;
    }

    public DealsStudentData fetchStudentData(Long customerId) {
        Criteria criteria = Criteria.where(CUSTOMER_ID).is(customerId);
        Query mongoQuery = new Query(criteria);
        DealsStudentData studentData =
                mongoOperations.findOne(mongoQuery, DealsStudentData.class);
        return studentData;
    }

    public DealsStudentData fetchStudentDataByRefId(String refId) {
        Criteria criteria = Criteria.where(REF_ID).is(refId);
        Query mongoQuery = new Query(criteria);
        DealsStudentData studentData =
                mongoOperations.findOne(mongoQuery, DealsStudentData.class);
        return studentData;
    }
}
