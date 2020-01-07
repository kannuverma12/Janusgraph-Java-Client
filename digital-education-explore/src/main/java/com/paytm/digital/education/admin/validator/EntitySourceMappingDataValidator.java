package com.paytm.digital.education.admin.validator;

import com.paytm.digital.education.admin.request.EntitySourceMappingData;
import com.paytm.digital.education.admin.request.EntitySourceMappingRequest;
import com.paytm.digital.education.constant.ExploreConstants;
import com.paytm.digital.education.constant.SchoolConstants;
import com.paytm.digital.education.database.entity.Course;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.entity.PaytmSourceDataEntity;
import com.paytm.digital.education.database.entity.School;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.database.repository.PaytmSourceDataRepository;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.EntitySourceType;
import com.paytm.digital.education.exception.EducationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_MERCHANT_SOURCE_MAPPING_DATA;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_PAYTM_SOURCE_MAPPING_DATA;

@Component
@AllArgsConstructor
public class EntitySourceMappingDataValidator {

    private PaytmSourceDataRepository paytmSourceDataRepository;
    private CommonMongoRepository     commonMongoRepository;

    public void validateEntitySourceMappingData(
            EntitySourceMappingRequest entitySourceMappingRequest) {
        EducationEntity entity = entitySourceMappingRequest.getEducationEntity();
        switch (entity) {
            case EXAM:
                validateExamEntityExistsForMapping(entitySourceMappingRequest);
                break;
            case INSTITUTE:
                validateInstituteEntityExistsForMapping(entitySourceMappingRequest);
                break;
            case SCHOOL:
                validateSchoolEntityExistsForMapping(entitySourceMappingRequest);
                break;
            case COURSE:
                validateCourseEntityExistsForMapping(entitySourceMappingRequest);
                break;
            default:
                break;
        }
    }

    private void validateExamEntityExistsForMapping(
            EntitySourceMappingRequest entitySourceMappingRequest) {
        for (EntitySourceMappingData entitySourceMappingData : entitySourceMappingRequest
                .getEntitySourceMappingData()) {
            if (EntitySourceType.PAYTM.equals(entitySourceMappingData.getSource())) {
                PaytmSourceDataEntity examEntity = paytmSourceDataRepository
                        .findByEntityIdAndEducationEntity(
                                entitySourceMappingData.getEntityId(),
                                EducationEntity.EXAM.name());
                if (Objects.isNull(examEntity)) {
                    throw new EducationException(INVALID_PAYTM_SOURCE_MAPPING_DATA,
                            new Object[] {entitySourceMappingData.getEntityId().toString()});
                }
            } else if (EntitySourceType.C360.equals(entitySourceMappingData.getSource())) {
                Exam exam = commonMongoRepository.getEntityById(ExploreConstants.EXAM_ID,
                        entitySourceMappingData.getEntityId(),
                        Exam.class);
                if (Objects.isNull(exam)) {
                    throw new EducationException(INVALID_MERCHANT_SOURCE_MAPPING_DATA,
                            new Object[] {entitySourceMappingData.getEntityId().toString()});
                }
            }
        }
    }

    private void validateCourseEntityExistsForMapping(
            EntitySourceMappingRequest entitySourceMappingRequest) {
        for (EntitySourceMappingData entitySourceMappingData : entitySourceMappingRequest
                .getEntitySourceMappingData()) {
            if (EntitySourceType.PAYTM.equals(entitySourceMappingData.getSource())) {
                PaytmSourceDataEntity courseEntity = paytmSourceDataRepository
                        .findByEntityIdAndEducationEntity(
                                entitySourceMappingData.getEntityId(),
                                EducationEntity.COURSE.name());

                if (Objects.isNull(courseEntity)) {
                    throw new EducationException(INVALID_PAYTM_SOURCE_MAPPING_DATA,
                            new Object[] {entitySourceMappingData.getEntityId().toString()});
                }
            } else if (EntitySourceType.C360.equals(entitySourceMappingData.getSource())) {
                Course course = commonMongoRepository.getEntityById(ExploreConstants.COURSE_ID,
                        entitySourceMappingData.getEntityId(),
                        Course.class);
                if (Objects.isNull(course)) {
                    throw new EducationException(INVALID_MERCHANT_SOURCE_MAPPING_DATA,
                            new Object[] {entitySourceMappingData.getEntityId().toString()});
                }
            }
        }
    }

    private void validateInstituteEntityExistsForMapping(
            EntitySourceMappingRequest entitySourceMappingRequest) {
        for (EntitySourceMappingData entitySourceMappingData : entitySourceMappingRequest
                .getEntitySourceMappingData()) {
            if (EntitySourceType.PAYTM.equals(entitySourceMappingData.getSource())) {
                PaytmSourceDataEntity instituteEntity = paytmSourceDataRepository
                        .findByEntityIdAndEducationEntity(
                                entitySourceMappingData.getEntityId(),
                                EducationEntity.INSTITUTE.name());
                if (Objects.isNull(instituteEntity)) {
                    throw new EducationException(INVALID_PAYTM_SOURCE_MAPPING_DATA,
                            new Object[] {entitySourceMappingData.getEntityId().toString()});
                }
            } else if (EntitySourceType.C360.equals(entitySourceMappingData.getSource())) {
                Institute institute =
                        commonMongoRepository.getEntityById(ExploreConstants.INSTITUTE_ID,
                                entitySourceMappingData.getEntityId(),
                                Institute.class);
                if (Objects.isNull(institute)) {
                    throw new EducationException(INVALID_MERCHANT_SOURCE_MAPPING_DATA,
                            new Object[] {entitySourceMappingData.getEntityId().toString()});
                }
            }
        }
    }

    private void validateSchoolEntityExistsForMapping(
            EntitySourceMappingRequest entitySourceMappingRequest) {
        for (EntitySourceMappingData entitySourceMappingData : entitySourceMappingRequest
                .getEntitySourceMappingData()) {
            if (EntitySourceType.PAYTM.equals(entitySourceMappingData.getSource())) {
                PaytmSourceDataEntity schoolEntity = paytmSourceDataRepository
                        .findByEntityIdAndEducationEntity(
                                entitySourceMappingData.getEntityId(),
                                EducationEntity.SCHOOL.name());
                if (Objects.isNull(schoolEntity)) {
                    throw new EducationException(INVALID_PAYTM_SOURCE_MAPPING_DATA,
                            new Object[] {entitySourceMappingData.getEntityId().toString()});
                }
            } else if (EntitySourceType.C360.equals(entitySourceMappingData.getSource())) {
                School school = commonMongoRepository.getEntityById(SchoolConstants.SCHOOL_ID,
                        entitySourceMappingData.getEntityId(),
                        School.class);
                if (Objects.isNull(school)) {
                    throw new EducationException(INVALID_MERCHANT_SOURCE_MAPPING_DATA,
                            new Object[] {entitySourceMappingData.getEntityId().toString()});
                }
            }
        }
    }
}
