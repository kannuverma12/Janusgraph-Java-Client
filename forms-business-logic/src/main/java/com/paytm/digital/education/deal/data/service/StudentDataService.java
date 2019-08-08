package com.paytm.digital.education.deal.data.service;

import static com.paytm.digital.education.deal.enums.StudentStatus.NO_SUCH_USER;
import static com.paytm.digital.education.deal.enums.StudentStatus.OTP_VERIFIED;
import static com.paytm.digital.education.deal.enums.StudentStatus.REGISTERED;
import static com.paytm.digital.education.deal.enums.StudentStatus.VERIFICATION_PENDING;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_CUSTOMER_ID;

import com.paytm.digital.education.deal.database.entity.DealsStudentData;
import com.paytm.digital.education.deal.database.repository.DealsStudentDataRepository;
import com.paytm.digital.education.deal.dto.response.VerificationStatusResponse;
import com.paytm.digital.education.deal.enums.StudentStatus;
import com.paytm.digital.education.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class StudentDataService {

    private DealsStudentDataRepository studentDataRepository;

    public DealsStudentData addStudentData(DealsStudentData studentData) {
        if (StringUtils.isNotBlank(studentData.getRefId())) {
            DealsStudentData dbStudentData =
                    studentDataRepository.fetchStudentDataByRefId(studentData.getRefId());
            if (Objects.nonNull(dbStudentData)) {
                copyNonNullProperties(dbStudentData, studentData);
                return studentDataRepository.saveOrUpdateStudentData(dbStudentData);
            }
        }
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

    public VerificationStatusResponse getStudentVerificationResponse(Long customerId) {
        DealsStudentData studentData = studentDataRepository.fetchStudentData(customerId);
        VerificationStatusResponse verificationResponse = new VerificationStatusResponse();
        if (Objects.nonNull(studentData)) {
            verificationResponse.setStatus(getStudentStatus(studentData));
            verificationResponse.setDealsStudentData(studentData);
        } else {
            verificationResponse.setStatus(NO_SUCH_USER);
            verificationResponse.setMessage("No such user exists.");
        }
        return verificationResponse;
    }

    private StudentStatus getStudentStatus(DealsStudentData studentData) {
        if (Objects.nonNull(studentData.getStudentIdentity())) {
            return VERIFICATION_PENDING;
        }
        if (Objects.nonNull(studentData.getMobileVerified()) && Objects
                .nonNull(studentData.getEmailVerified())) {
            return (studentData.getMobileVerified() && studentData.getEmailVerified())
                    ? OTP_VERIFIED : REGISTERED;
        }
        return studentData.getStatus();
    }

    private void copyNonNullProperties(DealsStudentData dbStudentData,
            DealsStudentData studentData) {
        try {
            PropertyUtils.describe(studentData).entrySet().stream()
                    .filter(e -> e.getValue() != null)
                    .filter(e -> !e.getKey().equals("class"))
                    .forEach(e -> {
                        try {
                            PropertyUtils.setProperty(dbStudentData, e.getKey(), e.getValue());
                        } catch (Exception e22) {
                            log.error("Error setting properties : {}", e22.getMessage());
                        }
                    });

        } catch (Exception e1) {
            log.error("Error setting properties : {}", e1.getMessage());
        }

    }
}
