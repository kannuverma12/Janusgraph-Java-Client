package com.paytm.digital.education.admin.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.digital.education.admin.request.PaytmSourceDataRequest;
import com.paytm.digital.education.admin.service.impl.PaytmSourceDataServiceImpl;
import com.paytm.digital.education.database.entity.Course;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.entity.PaytmSourceData;
import com.paytm.digital.education.database.entity.School;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;


@Component
public class PaytmSourceDataValidator {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger       log          =
            LoggerFactory.getLogger(PaytmSourceDataValidator.class);

    public void validateRequest(PaytmSourceDataRequest paytmSourceDataRequest) {

        if (CollectionUtils.isEmpty(paytmSourceDataRequest.getPaytmSourceData()) || Objects
                .isNull(paytmSourceDataRequest.getEducationEntity())) {
            throw new EducationException(ErrorEnum.NO_PAYTM_SOURCE_DATA, new String[] {});
        }
        switch (paytmSourceDataRequest.getEducationEntity()) {
            case EXAM:
                validateExamEntities(paytmSourceDataRequest);
                break;

            case SCHOOL:
                validateSchoolEntities(paytmSourceDataRequest);
                break;

            case INSTITUTE:
                validateInstituteEntities(paytmSourceDataRequest);
                break;

            case COURSE:
                validateCourseEntities(paytmSourceDataRequest);
                break;

            default:
                log.info("Invalid Entity : {}", paytmSourceDataRequest.getEducationEntity());
                throw new EducationException(ErrorEnum.INVALID_ENTITY_FOR_DATA_IMPORT,
                        ErrorEnum.INVALID_ENTITY_FOR_DATA_IMPORT.getExternalMessage());
        }
        return;
    }

    private void validateCourseEntities(PaytmSourceDataRequest paytmSourceDataRequest) {
        for (PaytmSourceData paytmSourceData : paytmSourceDataRequest
                .getPaytmSourceData()) {
            Map<String, Object> courseEntityData = paytmSourceData.getData();
            String courseId = paytmSourceData.getEntityId().toString();
            try {

                Course course = objectMapper.convertValue(courseEntityData, Course.class);
                if (Objects.nonNull(course) && Objects.nonNull(course.getCourseId())
                        && course.getCourseId().compareTo(paytmSourceData.getEntityId())
                        == 0) {
                    continue;
                }
            } catch (Exception e) {
                log.error("Exception occured while validating entityId : {}, entity : {}",
                        courseId, paytmSourceData.getEducationEntity());
            }
            throw new EducationException(ErrorEnum.INVALID_PAYTM_SOURCE_DATA,
                    new String[] {courseId});
        }
    }

    private void validateInstituteEntities(PaytmSourceDataRequest paytmSourceDataRequest) {
        for (PaytmSourceData paytmSourceData : paytmSourceDataRequest
                .getPaytmSourceData()) {
            Map<String, Object> instituteEntityData = paytmSourceData.getData();
            String instituteId = paytmSourceData.getEntityId().toString();
            try {

                Institute institute =
                        objectMapper.convertValue(instituteEntityData, Institute.class);
                if (Objects.nonNull(institute) && Objects
                        .nonNull(institute.getInstituteId())
                        &&
                        institute.getInstituteId().compareTo(paytmSourceData.getEntityId())
                                == 0) {
                    continue;
                }
            } catch (Exception e) {
                log.error("Exception occured while validating entityId : {}, entity : {}",
                        instituteId, paytmSourceData.getEducationEntity());
            }
            throw new EducationException(ErrorEnum.INVALID_PAYTM_SOURCE_DATA,
                    new String[] {instituteId});
        }
    }

    private void validateSchoolEntities(PaytmSourceDataRequest paytmSourceDataRequest) {
        for (PaytmSourceData paytmSourceData : paytmSourceDataRequest
                .getPaytmSourceData()) {
            Map<String, Object> schoolEntityData = paytmSourceData.getData();
            String schoolId = paytmSourceData.getEntityId().toString();
            try {

                School school =
                        objectMapper.convertValue(schoolEntityData, School.class);
                if (Objects.nonNull(school) && Objects.nonNull(school.getSchoolId())
                        && school.getSchoolId().compareTo(paytmSourceData.getEntityId())
                        == 0) {
                    continue;
                }
            } catch (Exception e) {
                log.error("Exception occured while validating entityId : {}, entity : {}",
                        schoolId, paytmSourceData.getEducationEntity());
            }
            throw new EducationException(ErrorEnum.INVALID_PAYTM_SOURCE_DATA,
                    new String[] {schoolId});
        }
    }

    private void validateExamEntities(PaytmSourceDataRequest paytmSourceDataRequest) {
        for (PaytmSourceData paytmSourceData : paytmSourceDataRequest
                .getPaytmSourceData()) {
            Map<String, Object> examEntityData = paytmSourceData.getData();
            String examId = paytmSourceData.getEntityId().toString();
            try {

                Exam exam = objectMapper.convertValue(examEntityData, Exam.class);
                if (Objects.nonNull(exam) && Objects.nonNull(exam.getExamId())
                        && exam.getExamId().compareTo(paytmSourceData.getEntityId()) == 0) {
                    continue;
                }
            } catch (Exception e) {
                log.error("Exception occured while validating entityId : {}, entity : {}",
                        examId, paytmSourceData.getEducationEntity());
            }
            throw new EducationException(ErrorEnum.INVALID_PAYTM_SOURCE_DATA,
                    new String[] {examId});
        }
    }
}
