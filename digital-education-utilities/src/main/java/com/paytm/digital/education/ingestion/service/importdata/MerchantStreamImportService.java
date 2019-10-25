package com.paytm.digital.education.ingestion.service.importdata;

import static com.paytm.digital.education.ingestion.constant.IngestionConstants.EMPTY_STRING;
import static com.paytm.digital.education.ingestion.constant.IngestionConstants.MERCHANT_STREAM_COMPONENT;
import static com.paytm.digital.education.ingestion.constant.IngestionConstants.MERCHANT_STREAM_SHEET_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_MERCHANT_STREAM_DATA;

import com.paytm.digital.education.database.entity.MerchantStreamEntity;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.ingestion.request.DataImportPropertiesRequest;
import com.paytm.digital.education.ingestion.response.DataImportPropertiesResponse;
import com.paytm.digital.education.ingestion.response.ImportResponse;
import com.paytm.digital.education.ingestion.service.MerchantStreamManagerService;
import com.paytm.digital.education.ingestion.sheets.MerchantStreamForm;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class MerchantStreamImportService extends AbstractImportService implements ImportService {

    private static final String TYPE = "MerchantStream";

    private static Logger log =
            LoggerFactory.getLogger(MerchantStreamImportService.class);

    @Autowired
    private MerchantStreamManagerService merchantStreamManagerService;

    @Override
    public ImportResponse ingest() {
        final DataImportPropertiesResponse dataImportPropertiesResponse = this.getProperties(
                MerchantStreamForm.class,
                DataImportPropertiesRequest.builder()
                        .sheetIdKey(MERCHANT_STREAM_SHEET_ID)
                        .build());

        final List<Object> streamFormData = this.getFormData(dataImportPropertiesResponse);

        final List<MerchantStreamForm> failedStreamFormList = new ArrayList<>();

        return this.processRecords(streamFormData,
                failedStreamFormList, MerchantStreamForm.class, TYPE);
    }

    @Override
    protected <T> void upsertFailedRecords(T form, Class<T> clazz) {
        final MerchantStreamForm newStreamForm = (MerchantStreamForm) clazz.cast(form);
        try {
            validateMerchantStreamRequest(newStreamForm);
            this.merchantStreamManagerService.createOrUpdateMerchantStream(newStreamForm);
        } catch (final Exception e) {
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", e, form);
        }
    }

    @Override
    protected <T> void upsertNewRecords(T form, List<Object> failedDataList,
            Class<T> clazz) {
        final MerchantStreamForm newStreamForm = (MerchantStreamForm) clazz.cast(form);
        MerchantStreamEntity entityResponse = null;
        String failureMessage = EMPTY_STRING;
        try {
            validateMerchantStreamRequest(newStreamForm);
            entityResponse = this.merchantStreamManagerService.createOrUpdateMerchantStream(newStreamForm);
        } catch (final Exception e) {
            log.error("Got Exception in upsertNewRecords for input: {}, exception: ", e, form);
            failureMessage = e.getMessage();
        }

        if (Objects.isNull(entityResponse) || StringUtils.isNotBlank(failureMessage)) {
            log.error("Response: {}", entityResponse);
            this.addToFailedList(newStreamForm,
                    failureMessage, true, MERCHANT_STREAM_COMPONENT, TYPE, failedDataList);
        }
    }

    private void validateMerchantStreamRequest(MerchantStreamForm merchantStreamForm) {
        if (StringUtils.isBlank(merchantStreamForm.getMerchantStream())
                || StringUtils.isBlank(merchantStreamForm.getMerchantId())
                || Objects.isNull(merchantStreamForm.getPaytmStreamId())
                || merchantStreamForm.getPaytmStreamId() < 0) {
            throw new BadRequestException(INVALID_MERCHANT_STREAM_DATA,
                    INVALID_MERCHANT_STREAM_DATA.getExternalMessage());
        }
    }

}
