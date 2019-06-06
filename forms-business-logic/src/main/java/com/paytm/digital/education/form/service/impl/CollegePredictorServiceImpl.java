package com.paytm.digital.education.form.service.impl;

import static com.paytm.digital.education.form.constants.FblConstants.DATA_MERCHANT_SKU;
import static com.paytm.digital.education.form.constants.FblConstants.ERROR;
import static com.paytm.digital.education.form.constants.FblConstants.PAYMENT_AMOUNT;
import static com.paytm.digital.education.form.constants.FblConstants.PAYTM_PRODUCT_ID;
import static com.paytm.digital.education.form.constants.FblConstants.PAYTM_PRODUCT_NAME;
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
import com.paytm.digital.education.form.model.CollegePredictor;
import com.paytm.digital.education.form.repository.PredictorListRepository;
import com.paytm.digital.education.form.response.PredictorListResponse;
import com.paytm.digital.education.form.service.CollegePredictorService;
import com.paytm.digital.education.form.service.MerchantProductConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CollegePredictorServiceImpl implements CollegePredictorService {

    @Value("${catalog.predictor.mid}")
    private String paytmMid;

    @Autowired
    private FormDataRepository           formDataRepository;

    @Autowired
    private MerchantProductConfigService merchantProductConfigService;

    @Autowired
    private PredictorListRepository      predictorListRepository;

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
            }
        } else {
            formData.setId(null);
            formData.setCreatedAt(new Date());
            formData.setUpdatedAt(new Date());
            dbFormData = formData;
        }
        updateOtherDetails(formData, responseDataMap);

        dbFormData = formDataRepository.save(dbFormData);
        if (StringUtils.isNotBlank(dbFormData.getId())) {
            responseDataMap.put(REFERENCE_ID, dbFormData.getId());
            processResponseData(dbFormData, responseDataMap);
        }
        return responseDataMap;
    }

    private void updateOtherDetails(FormData formData, Map<String, Object> responseDataMap) {
        Map<String, Object> merchantResponse =
                formData.getCandidateDetails().getResponseData().get(0);
        String merchantProductId = String.valueOf(
                formData.getCandidateDetails().getRequestData().get(0).get(PRODUCT_ID));
        formData.setMerchantCandidateId(merchantResponse.get(RN_TOKEN).toString());
        formData.setMerchantProductId(merchantProductId);

        Boolean renderForm2 = (boolean) merchantResponse.get(RENDER_FORM2);
        if (Objects.isNull(renderForm2) || renderForm2 == false) {
            MerchantProductConfig merchantProductConfig = merchantProductConfigService
                    .getConfigByMerchantIdAndKey(formData.getMerchantId(), DATA_MERCHANT_SKU,
                            merchantProductId, new ArrayList<>());
            Float amountToPay = getProductPrice(formData, merchantProductConfig);
            formData.getCandidateDetails().setAmount(amountToPay);
            formData.getCandidateDetails().setPredictorName(
                    merchantProductConfig.getData().get(PAYTM_PRODUCT_NAME).toString());
            responseDataMap.put(PAYMENT_AMOUNT, amountToPay);
            responseDataMap.put(PAYTM_PRODUCT_ID, merchantProductConfig.getProductId());
            updateMaskedData(formData);
        }
    }

    private void updateMaskedData(FormData formData) {
        StringBuilder emailId = new StringBuilder(formData.getCandidateDetails().getEmail());
        if (StringUtils.isNotBlank(emailId)) {
            String emailUserId = emailId.substring(0, emailId.indexOf("@"));
            int startLen = emailUserId.length() * 30 / 100;
            for (int i = startLen; i < emailUserId.length(); i++) {
                emailId.setCharAt(i, 'X');
            }
            formData.getCandidateDetails().setMaskEmail(emailId.toString());
        }
    }

    @Override
    public PredictorListResponse getPredictorList() {
        List<CollegePredictor> predictorList = predictorListRepository.findAll();
        Set<String> pids = predictorList.stream().filter(p -> Objects.nonNull(p.getPid()))
                .map(p -> String.valueOf(p.getPid())).collect(Collectors.toSet());

        List<MerchantProductConfig> mpcList = merchantProductConfigService.getAllConfigs(paytmMid,
                pids, new ArrayList<>());

        Map<Long, Integer> pidPriceMap = getPidPriceMap(mpcList);
        if (Objects.nonNull(pidPriceMap)) {
            for (CollegePredictor cp : predictorList) {
                if (pidPriceMap.containsKey(cp.getPid())) {
                    cp.setPaytmPrice(pidPriceMap.get(cp.getPid()));
                }
            }
        }
        if (!CollectionUtils.isEmpty(predictorList)) {
            return new PredictorListResponse(200, predictorList);
        }
        return null;
    }

    private Map<Long, Integer> getPidPriceMap(List<MerchantProductConfig> mpcList) {
        Map<Long, Integer> pidPriceMap = new HashMap<>();

        if (Objects.nonNull(mpcList)) {
            for (MerchantProductConfig mpc : mpcList) {
                if (Objects.nonNull(mpc.getData())) {
                    Map<String, Object> data = mpc.getData();
                    if (data.containsKey(PAYMENT_AMOUNT)) {
                        Double payAmount = (Double) data.get(PAYMENT_AMOUNT);
                        pidPriceMap.put(Long.valueOf(mpc.getProductId()), payAmount.intValue());
                    }
                }
            }
        }

        if (!CollectionUtils.isEmpty(pidPriceMap)) {
            return pidPriceMap;
        }
        return null;
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

            responseDataMap.put(STATUS_CODE, 200);
            responseDataMap.putAll(merchantResponseData);
        }
    }

    private Float getProductPrice(FormData formData, MerchantProductConfig merchantProductConfig) {
        List<FormData> paymentMadeFormsData = formDataRepository
                .getFormsDataByPaymentStatus(formData.getCustomerId(), formData.getMerchantId(),
                        formData.getMerchantProductId(), SUCCESS_STRING);
        if (CollectionUtils.isEmpty(paymentMadeFormsData)) {
            if (Objects.isNull(merchantProductConfig) || Objects
                    .isNull(merchantProductConfig.getData().get(PAYMENT_AMOUNT))) {
                throw new EducationException(PAYMENT_CONFIGURATION_NOT_FOUND,
                        PAYMENT_CONFIGURATION_NOT_FOUND.getExternalMessage(),
                        new Object[] {formData.getMerchantId(),
                                formData.getMerchantProductId()});
            }
            return Float.valueOf(merchantProductConfig.getData().get(PAYMENT_AMOUNT).toString());
        }
        return 0f;
    }

    private boolean validateFormDataRequest(FormData formData) {
        return StringUtils.isNotBlank(formData.getId())
                || (StringUtils.isNotBlank(formData.getMerchantId())
                && StringUtils.isNotBlank(formData.getCustomerId()));
    }
}
