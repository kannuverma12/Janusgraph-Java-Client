package com.paytm.digital.education.form.service.impl;

import static com.paytm.digital.education.form.constants.FblConstants.ERROR;
import static com.paytm.digital.education.form.constants.FblConstants.PAYMENT;
import static com.paytm.digital.education.form.constants.FblConstants.PAYMENT_AMOUNT;
import static com.paytm.digital.education.form.constants.FblConstants.PRODUCT_ID;
import static com.paytm.digital.education.form.constants.FblConstants.REFERENCE_ID;
import static com.paytm.digital.education.form.constants.FblConstants.RENDER_FORM2;
import static com.paytm.digital.education.form.constants.FblConstants.RN_TOKEN;
import static com.paytm.digital.education.form.constants.FblConstants.STATUS;
import static com.paytm.digital.education.form.constants.FblConstants.STATUS_CODE;
import static com.paytm.digital.education.form.constants.FblConstants.SUCCESS_STRING;
import static com.paytm.digital.education.form.constants.FblConstants.UNAUTHORIZED;
import static com.paytm.digital.education.mapping.ErrorEnum.MISSING_FORM_DATA_PARAMS;
import static com.paytm.digital.education.mapping.ErrorEnum.PAYMENT_CONFIGURATION_NOT_FOUND;
import static com.paytm.digital.education.mapping.ErrorEnum.UNAUTHORIZED_REQUEST;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.model.MerchantProductConfig;
import com.paytm.digital.education.form.repository.FormDataRepository;
import com.paytm.digital.education.form.service.CollegePredictorService;
import com.paytm.digital.education.form.service.MerchantProductConfigService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class CollegePredictorServiceImpl implements CollegePredictorService {

    private FormDataRepository           formDataRepository;
    private MerchantProductConfigService merchantProductConfigService;

    @Override
    public Map<String, Object> savePredictorFormData(FormData formData) {
        if (!validateFormDataRequest(formData)) {
            throw new BadRequestException(MISSING_FORM_DATA_PARAMS,
                    MISSING_FORM_DATA_PARAMS.getExternalMessage());
        }
        Map<String, Object> responseDataMap = new HashMap<>();
        FormData dbFormData = null;
        if (StringUtils.isNotBlank(formData.getId())) {
            Optional<FormData> formDataOptional = formDataRepository.findById(formData.getId());
            if (formDataOptional.isPresent()) {
                dbFormData = formDataOptional.get();
            }
            if (Objects.nonNull(dbFormData)) {
                if (!CollectionUtils.isEmpty(formData.getCandidateDetails().getRequestData())) {
                    if (CollectionUtils.isEmpty(dbFormData.getCandidateDetails().getRequestData())) {
                        dbFormData.getCandidateDetails().setRequestData(new ArrayList<>());
                    }
                    dbFormData.getCandidateDetails().getRequestData()
                            .addAll(formData.getCandidateDetails().getRequestData());
                }

                if (!CollectionUtils.isEmpty(formData.getCandidateDetails().getResponseData())) {
                    if (CollectionUtils
                            .isEmpty(dbFormData.getCandidateDetails().getResponseData())) {
                        dbFormData.getCandidateDetails().setResponseData(new ArrayList<>());
                    }
                    dbFormData.getCandidateDetails().getResponseData()
                            .addAll(formData.getCandidateDetails().getResponseData());
                }
                dbFormData.setUpdatedAt(new Date());
                dbFormData = formDataRepository.save(dbFormData);
            }
        } else {
            formData.setId(null);
            formData.setCreatedAt(new Date());
            formData.setUpdatedAt(new Date());
            if (!CollectionUtils.isEmpty(formData.getCandidateDetails().getResponseData())) {
                Map<String, Object> merchantResponse =
                        formData.getCandidateDetails().getResponseData().get(0);
                formData.setMerchantCandidateId(merchantResponse.get(RN_TOKEN).toString());
            }
            String merchantProductId = String.valueOf(
                    formData.getCandidateDetails().getRequestData().get(0).get(PRODUCT_ID));
            formData.setMerchantProductId(merchantProductId);
            dbFormData = formDataRepository.save(formData);
        }
        if (Objects.nonNull(dbFormData) && StringUtils.isNotBlank(dbFormData.getId())) {
            responseDataMap.put(REFERENCE_ID, dbFormData.getId());
            processResponseData(dbFormData, responseDataMap);
        }
        return responseDataMap;
    }

    private void processResponseData(FormData formData, Map<String, Object> responseDataMap) {
        if (!CollectionUtils.isEmpty(formData.getCandidateDetails().getResponseData())) {
            Map<String, Object> merchantResponseData =
                    formData.getCandidateDetails().getResponseData().get(0);
            if (merchantResponseData.containsKey(STATUS) && ((String) merchantResponseData
                    .get(STATUS)).equalsIgnoreCase(UNAUTHORIZED)) {
                throw new EducationException(UNAUTHORIZED_REQUEST,
                        UNAUTHORIZED_REQUEST.getExternalMessage());
            }

            if (merchantResponseData.containsKey(ERROR) && StringUtils
                    .isNotBlank(merchantResponseData.get(ERROR).toString())) {
                throw new BadRequestException(MISSING_FORM_DATA_PARAMS,
                        merchantResponseData.get(ERROR).toString());
            }

            boolean renderForm2 = (boolean) merchantResponseData.get(RENDER_FORM2);
            if (renderForm2 == false) {
                updatePaymentStatus(formData, responseDataMap);
            }


            if (Objects.nonNull(formData.getFormFulfilment()) && Objects
                    .nonNull(formData.getFormFulfilment().getProductId())) {
                responseDataMap.put(PRODUCT_ID, formData.getFormFulfilment().getProductId());
            }
            responseDataMap.put(STATUS_CODE, 200);
            responseDataMap.putAll(merchantResponseData);
        }
    }

    private void updatePaymentStatus(FormData formData, Map<String, Object> responseDataMap) {
        Float amountToPay = getProductPrice(formData);
        responseDataMap.put(PAYMENT_AMOUNT, amountToPay);
    }


    private Float getProductPrice(FormData formData) {
        List<FormData> paymentMadeFormsData = formDataRepository
                .getFormsDataByPaymentStatus(formData.getCustomerId(), formData.getMerchantId(),
                        formData.getMerchantProductId(), SUCCESS_STRING);
        if (CollectionUtils.isEmpty(paymentMadeFormsData)) {
            Float amountToPay = getPaymentAmount(formData);
            if (Objects.isNull(amountToPay)) {
                throw new EducationException(PAYMENT_CONFIGURATION_NOT_FOUND,
                        PAYMENT_CONFIGURATION_NOT_FOUND.getExternalMessage(),
                        new Object[] {formData.getMerchantId(), formData.getFormFulfilment().getProductId()});
            }
            return amountToPay;
        }
        return 0f;
    }

    private Float getPaymentAmount(FormData formData) {
        String catalogProductId = String.valueOf(formData.getFormFulfilment().getProductId());
        String merchantId = formData.getMerchantId();
        MerchantProductConfig merchantProductConfig = merchantProductConfigService
                .getConfig(merchantId, catalogProductId, new ArrayList<>());
        if (Objects.nonNull(merchantProductConfig) && Objects
                .nonNull(merchantProductConfig.getData().get(PAYMENT))) {
            Map<String, Object> paymentConfig =
                    (Map<String, Object>) merchantProductConfig.getData().get(PAYMENT);
            String merchantProductId = formData.getMerchantProductId();
            Map<String, Object> productPaymentConfig =
                    (Map<String, Object>) paymentConfig.get(merchantProductId);
            if (Objects.nonNull(productPaymentConfig) && Objects
                    .nonNull(productPaymentConfig.get(PAYMENT_AMOUNT))) {
                return Float.valueOf(productPaymentConfig.get(PAYMENT_AMOUNT).toString());
            }
        }
        return null;
    }

    private boolean validateFormDataRequest(FormData formData) {
        return StringUtils.isNotBlank(formData.getId())
                || (StringUtils.isNotBlank(formData.getMerchantId())
                && StringUtils.isNotBlank(formData.getCustomerId()));
    }
}
