package com.paytm.digital.education.admin.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.digital.education.admin.request.PaytmSourceDataRequest;
import com.paytm.digital.education.admin.response.MerchantSourceResponse;
import com.paytm.digital.education.admin.response.PaytmSourceDataResponse;
import com.paytm.digital.education.admin.response.PaytmSourceResponse;
import com.paytm.digital.education.admin.validator.PaytmSourceDataValidator;
import com.paytm.digital.education.constant.ExploreConstants;
import com.paytm.digital.education.constant.SchoolConstants;
import com.paytm.digital.education.database.entity.Course;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.entity.PaytmSourceData;
import com.paytm.digital.education.database.entity.PaytmSourceDataEntity;
import com.paytm.digital.education.database.entity.School;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.database.repository.ExamRepository;
import com.paytm.digital.education.database.repository.PaytmSourceDataRepository;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.EntitySourceType;
import com.paytm.digital.education.explore.database.repository.InstituteRepository;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.utility.CommonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.paytm.digital.education.constant.ExploreConstants.FAILED;
import static com.paytm.digital.education.constant.ExploreConstants.PARTIAL_FAILED;
import static com.paytm.digital.education.constant.ExploreConstants.SUCCESS;

@Service
@AllArgsConstructor
public class PaytmSourceDataServiceImpl {

    private static final Logger                    log    =
            LoggerFactory.getLogger(PaytmSourceDataServiceImpl.class);
    private static final ObjectMapper              mapper = new ObjectMapper();
    private              PaytmSourceDataRepository paytmSourceDataRepository;
    private              PaytmSourceDataValidator  paytmSourceDataValidator;
    private              ExamRepository            examRepository;
    private              InstituteRepository       instituteRepository;
    private              CommonMongoRepository     commonMongoRepository;


    public PaytmSourceDataResponse savePaytmSourceData(
            PaytmSourceDataRequest paytmSourceDataRequest) {

        if (CollectionUtils.isEmpty(paytmSourceDataRequest.getPaytmSourceData())) {
            log.error("No paytm source data found in create request, skipping");
            return null;
        }

        paytmSourceDataValidator.validateRequest(paytmSourceDataRequest);
        PaytmSourceDataResponse paytmSourceDataResponse = new PaytmSourceDataResponse();
        List<PaytmSourceDataEntity> paytmSourceDataUpdatedList = new ArrayList<>();

        for (PaytmSourceData paytmSourceData : paytmSourceDataRequest.getPaytmSourceData()) {

            PaytmSourceDataEntity paytmSourceDataInDb = paytmSourceDataRepository
                    .findByEntityIdAndEducationEntityAndSource(paytmSourceData.getEntityId(),
                            paytmSourceDataRequest.getEducationEntity().name(),
                            EntitySourceType.PAYTM.name());

            if (Objects.isNull(paytmSourceDataInDb)) {
                paytmSourceDataInDb = new PaytmSourceDataEntity();
            }
            CommonUtils.copyNonNullProperties(paytmSourceData, paytmSourceDataInDb);
            paytmSourceDataInDb.setExamData(new Exam());

            switch (paytmSourceDataRequest.getEducationEntity()) {
                case EXAM:
                    Exam examDataToBeUpdated =
                            mapper.convertValue(paytmSourceData.getData(), Exam.class);
                    paytmSourceDataInDb.setExamData(examDataToBeUpdated);
                    break;
                case INSTITUTE:
                    Institute instituteDataToBeUpdated =
                            mapper.convertValue(paytmSourceData.getData(), Institute.class);
                    paytmSourceDataInDb.setInstituteData(instituteDataToBeUpdated);
                    break;
                case SCHOOL:
                    School schoolDataToBeUpdated =
                            mapper.convertValue(paytmSourceData.getData(), School.class);
                    paytmSourceDataInDb.setSchoolData(schoolDataToBeUpdated);
                    break;
                case COURSE:
                    Course coureDataToBeUpdated =
                            mapper.convertValue(paytmSourceData.getData(), Course.class);
                    paytmSourceDataInDb.setCourseData(coureDataToBeUpdated);
                    break;
                default:
                    log.error("Entity not supported for ingestion : {}",
                            paytmSourceDataRequest.getEducationEntity());
                    break;
            }

            paytmSourceDataInDb.setEducationEntity(paytmSourceDataRequest.getEducationEntity());
            paytmSourceDataInDb.setSource(EntitySourceType.PAYTM);
            paytmSourceDataInDb.setActive(true);

            PaytmSourceDataEntity paytmSourceDataUpdated =
                    paytmSourceDataRepository.save(paytmSourceDataInDb);
            paytmSourceDataUpdatedList.add(paytmSourceDataUpdated);
        }

        paytmSourceDataResponse.setEducationEntity(paytmSourceDataRequest.getEducationEntity());
        paytmSourceDataResponse.setPaytmSourceData(paytmSourceDataUpdatedList);
        paytmSourceDataResponse.setStatus(FAILED);

        if (!CollectionUtils.isEmpty(paytmSourceDataUpdatedList)
                && paytmSourceDataUpdatedList.size() == paytmSourceDataRequest.getPaytmSourceData()
                .size()) {
            paytmSourceDataResponse.setStatus(SUCCESS);
        } else if (!CollectionUtils.isEmpty(paytmSourceDataUpdatedList)
                && paytmSourceDataUpdatedList.size() > 0) {
            paytmSourceDataResponse.setStatus(PARTIAL_FAILED);
        }
        return paytmSourceDataResponse;
    }

    public PaytmSourceResponse getPaytmSourceData(EducationEntity entity, Long entityId) {
        PaytmSourceResponse paytmSourceDataResponse = new PaytmSourceResponse();
        paytmSourceDataResponse.setStatus(FAILED);

        PaytmSourceDataEntity paytmSourceData = paytmSourceDataRepository
                .findByEntityIdAndEducationEntityAndSource(entityId, entity.name(),
                        EntitySourceType.PAYTM.name());

        paytmSourceDataResponse.setStatus(FAILED);
        paytmSourceDataResponse.setMessage(ErrorEnum.NO_PAYTM_SOURCE_DATA.name());

        if (Objects.nonNull(paytmSourceData)) {
            paytmSourceDataResponse.setStatus(SUCCESS);
            paytmSourceDataResponse.setPaytmSourceData(paytmSourceData);
        }
        return paytmSourceDataResponse;
    }

    public PaytmSourceDataResponse deletePaytmSourceData(
            PaytmSourceDataRequest paytmSourceDataRequest) {
        if (CollectionUtils.isEmpty(paytmSourceDataRequest.getPaytmSourceData())) {
            log.error("No paytm source data found in delete request, skipping");
            return null;
        }
        PaytmSourceDataResponse paytmSourceDataResponse = new PaytmSourceDataResponse();
        List<PaytmSourceDataEntity> paytmSourceDataUpdatedList = new ArrayList<>();

        for (PaytmSourceData paytmSourceData : paytmSourceDataRequest.getPaytmSourceData()) {
            PaytmSourceDataEntity paytmSourceDataInDb = paytmSourceDataRepository
                    .findByEntityIdAndEducationEntityAndSource(paytmSourceData.getEntityId(),
                            paytmSourceDataRequest.getEducationEntity().name(),
                            EntitySourceType.PAYTM.name());

            if (Objects.isNull(paytmSourceDataInDb)) {
                log.info(
                        "Paytm source data is not present in db for entityId :{}, entity: {}, skipping",
                        paytmSourceData.getEntityId(),
                        paytmSourceDataRequest.getEducationEntity());
                continue;
            }
            paytmSourceDataInDb.setActive(false);
            PaytmSourceDataEntity paytmSourceDataUpdated =
                    paytmSourceDataRepository.save(paytmSourceDataInDb);
            paytmSourceDataUpdatedList.add(paytmSourceDataUpdated);
        }

        paytmSourceDataResponse.setEducationEntity(paytmSourceDataRequest.getEducationEntity());
        paytmSourceDataResponse.setPaytmSourceData(paytmSourceDataUpdatedList);
        paytmSourceDataResponse.setStatus(FAILED);

        if (!CollectionUtils.isEmpty(paytmSourceDataUpdatedList)
                && paytmSourceDataUpdatedList.size() == paytmSourceDataRequest.getPaytmSourceData()
                .size()) {
            paytmSourceDataResponse.setStatus(SUCCESS);
        } else if (!CollectionUtils.isEmpty(paytmSourceDataUpdatedList)
                && paytmSourceDataUpdatedList.size() > 0) {
            paytmSourceDataResponse.setStatus(PARTIAL_FAILED);
        }
        return paytmSourceDataResponse;
    }

    public MerchantSourceResponse getMerchantSourceData(EducationEntity entity, Long entityId) {
        MerchantSourceResponse merchantSourceResponse = new MerchantSourceResponse();
        merchantSourceResponse.setStatus(FAILED);
        switch (entity) {
            case EXAM:
                Exam exam = examRepository.findByExamId(entityId);

                if (Objects.nonNull(exam)) {
                    Map<String, Object> entityDatamap =
                            mapper.convertValue(exam, new TypeReference<Map<String, Object>>() {
                            });
                    merchantSourceResponse.setStatus(SUCCESS);
                    merchantSourceResponse.setMerchantSourceData(entityDatamap);
                }
                break;
            case INSTITUTE:
                Institute institute = instituteRepository.findByInstituteId(entityId);

                if (Objects.nonNull(institute)) {
                    Map<String, Object> entityDatamap =
                            mapper.convertValue(institute,
                                    new TypeReference<Map<String, Object>>() {
                                    });
                    merchantSourceResponse.setStatus(SUCCESS);
                    merchantSourceResponse.setMerchantSourceData(entityDatamap);
                }
                break;
            case SCHOOL:
                School school =
                        commonMongoRepository.getEntityById(SchoolConstants.SCHOOL_ID, entityId, School.class);
                if (Objects.nonNull(school)) {
                    Map<String, Object> entityDatamap =
                            mapper.convertValue(school, new TypeReference<Map<String, Object>>() {
                            });
                    merchantSourceResponse.setStatus(SUCCESS);
                    merchantSourceResponse.setMerchantSourceData(entityDatamap);
                }
                break;
            case COURSE:
                Course course =
                        commonMongoRepository.getEntityById(ExploreConstants.COURSE_ID, entityId, Course.class);
                if (Objects.nonNull(course)) {
                    Map<String, Object> entityDatamap =
                            mapper.convertValue(course, new TypeReference<Map<String, Object>>() {
                            });
                    merchantSourceResponse.setStatus(SUCCESS);
                    merchantSourceResponse.setMerchantSourceData(entityDatamap);
                }
                break;
            default:
                break;
        }
        return merchantSourceResponse;
    }
}
