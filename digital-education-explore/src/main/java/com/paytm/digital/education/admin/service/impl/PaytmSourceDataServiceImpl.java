package com.paytm.digital.education.admin.service.impl;

import com.paytm.digital.education.admin.controller.PaytmEntityDataAdminController;
import com.paytm.digital.education.admin.request.PaytmSourceDataRequest;
import com.paytm.digital.education.admin.response.PaytmSourceDataResponse;
import com.paytm.digital.education.constant.ExploreConstants;
import com.paytm.digital.education.database.entity.PaytmSourceData;
import com.paytm.digital.education.database.repository.PaytmSourceDataRepository;
import com.paytm.digital.education.enums.EntitySourceType;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class PaytmSourceDataServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(PaytmEntityDataAdminController.class);

    private PaytmSourceDataRepository paytmSourceDataRepository;

    public PaytmSourceDataResponse createPaytmSourceData(
            PaytmSourceDataRequest paytmSourceDataRequest) {

        PaytmSourceDataResponse paytmSourceDataResponse = new PaytmSourceDataResponse();
        List<PaytmSourceData> paytmSourceRequestDataList =
                paytmSourceDataRequest.getPaytmSourceData();

        if (CollectionUtils.isEmpty(paytmSourceRequestDataList)) {
            log.error("No paytm source data found in create request, skipping");
            return null;
        }

        for (PaytmSourceData paytmSourceData : paytmSourceRequestDataList) {
            PaytmSourceData paytmSourceDataInDb = paytmSourceDataRepository
                    .findByEntityIdAndEducationEntityAndSource(paytmSourceData.getEntityId(),
                            paytmSourceDataRequest.getEducationEntity().toString(),
                            EntitySourceType.PAYTM.getName());
            if (Objects.nonNull(paytmSourceDataInDb)) {
                paytmSourceRequestDataList.remove(paytmSourceData);
                continue;
            }
            paytmSourceData.setEducationEntity(paytmSourceDataRequest.getEducationEntity());
            paytmSourceData.setSource(EntitySourceType.PAYTM);
        }

        List<PaytmSourceData> paytmSourceDataUpdatedList =
                paytmSourceDataRepository.saveAll(paytmSourceRequestDataList);

        if (!CollectionUtils.isEmpty(paytmSourceDataUpdatedList)
                && paytmSourceDataUpdatedList.size() == paytmSourceRequestDataList.size()) {
            paytmSourceDataResponse.setStatus(ExploreConstants.SUCCESS);
        } else {
            paytmSourceDataResponse.setStatus(ExploreConstants.FAILED);
        }

        paytmSourceDataResponse.setEducationEntity(paytmSourceDataRequest.getEducationEntity());
        paytmSourceDataResponse.setPaytmSourceData(paytmSourceDataUpdatedList);

        return paytmSourceDataResponse;
    }
}
