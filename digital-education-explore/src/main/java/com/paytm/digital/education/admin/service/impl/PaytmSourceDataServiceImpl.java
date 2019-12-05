package com.paytm.digital.education.admin.service.impl;

import com.paytm.digital.education.admin.controller.PaytmSourceDataAdminController;
import com.paytm.digital.education.admin.request.PaytmSourceDataRequest;
import com.paytm.digital.education.admin.response.PaytmSourceResponse;
import com.paytm.digital.education.admin.response.PaytmSourceDataResponse;

import com.paytm.digital.education.database.entity.PaytmSourceData;
import com.paytm.digital.education.database.repository.PaytmSourceDataRepository;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.EntitySourceType;
import com.paytm.digital.education.utility.CommonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.paytm.digital.education.constant.ExploreConstants.SUCCESS;
import static com.paytm.digital.education.constant.ExploreConstants.FAILED;
import static com.paytm.digital.education.constant.ExploreConstants.PARTIAL_FAILED;

@Service
@AllArgsConstructor
public class PaytmSourceDataServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(PaytmSourceDataAdminController.class);

    private PaytmSourceDataRepository paytmSourceDataRepository;

    public PaytmSourceDataResponse savePaytmSourceData(
            PaytmSourceDataRequest paytmSourceDataRequest) {

        if (CollectionUtils.isEmpty(paytmSourceDataRequest.getPaytmSourceData())) {
            log.error("No paytm source data found in create request, skipping");
            return null;
        }

        PaytmSourceDataResponse paytmSourceDataResponse = new PaytmSourceDataResponse();
        List<PaytmSourceData> paytmSourceDataUpdatedList = new ArrayList<>();

        for (PaytmSourceData paytmSourceData : paytmSourceDataRequest.getPaytmSourceData()) {
            PaytmSourceData paytmSourceDataInDb = paytmSourceDataRepository
                    .findByEntityIdAndEducationEntityAndSource(paytmSourceData.getEntityId(),
                            paytmSourceDataRequest.getEducationEntity().name(),
                            EntitySourceType.PAYTM.name());
            if (Objects.nonNull(paytmSourceDataInDb)) {
                CommonUtils.copyNonNullProperties(paytmSourceData, paytmSourceDataInDb);
                paytmSourceData = paytmSourceDataInDb;
            }

            paytmSourceData.setEducationEntity(paytmSourceDataRequest.getEducationEntity());
            paytmSourceData.setSource(EntitySourceType.PAYTM);
            paytmSourceData.setActive(true);
            PaytmSourceData paytmSourceDataUpdated =
                    paytmSourceDataRepository.save(paytmSourceData);
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

        PaytmSourceData paytmSourceData = paytmSourceDataRepository
                .findByEntityIdAndEducationEntityAndSource(entityId, entity.name(),
                        EntitySourceType.PAYTM.name());

        paytmSourceDataResponse.setStatus(FAILED);

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
        List<PaytmSourceData> paytmSourceDataUpdatedList = new ArrayList<>();

        for (PaytmSourceData paytmSourceData : paytmSourceDataRequest.getPaytmSourceData()) {
            PaytmSourceData paytmSourceDataInDb = paytmSourceDataRepository
                    .findByEntityIdAndEducationEntityAndSource(paytmSourceData.getEntityId(),
                            paytmSourceDataRequest.getEducationEntity().name(),
                            EntitySourceType.PAYTM.name());
            paytmSourceDataInDb.setActive(false);
            paytmSourceDataRepository.save(paytmSourceDataInDb);
            paytmSourceDataUpdatedList.add(paytmSourceDataInDb);
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
}
