package com.paytm.digital.education.ingestion.service.importdata;

import static com.paytm.digital.education.ingestion.constant.IngestionConstants.EMPTY_STRING;
import static com.paytm.digital.education.ingestion.constant.IngestionConstants.EXAM_STREAM_COMPONENT;
import static com.paytm.digital.education.ingestion.constant.IngestionConstants.EXAM_STREAM_SHEET_ID;
import static com.paytm.digital.education.ingestion.constant.IngestionConstants.TYPE;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_ID_FOR_EXAM_NAME;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_PAYTM_STREAM;

import com.paytm.digital.education.database.entity.ExamStreamEntity;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.ingestion.request.DataImportPropertiesRequest;
import com.paytm.digital.education.ingestion.response.DataImportPropertiesResponse;
import com.paytm.digital.education.ingestion.response.ImportResponse;
import com.paytm.digital.education.ingestion.service.ExamStreamManagerService;
import com.paytm.digital.education.ingestion.sheets.ExamStreamForm;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ExamStreamImportService extends AbstractImportService implements ImportService {

    private static Logger log = LoggerFactory.getLogger(ExamStreamImportService.class);

    @Autowired
    private ExamStreamManagerService examStreamManagerService;

    @Override
    public ImportResponse ingest() {
        final DataImportPropertiesResponse dataImportPropertiesResponse = this.getProperties(
                ExamStreamForm.class,
                DataImportPropertiesRequest.builder()
                        .sheetIdKey(EXAM_STREAM_SHEET_ID)
                        .build());
        final List<Object> streamFormData = this.getFormData(dataImportPropertiesResponse);

        final List<ExamStreamForm> failedStreamFormList = new ArrayList<>();
        return this.processRecords(streamFormData,
                failedStreamFormList, ExamStreamForm.class, TYPE);
    }

    @Override
    protected <T> void upsertFailedRecords(T form, Class<T> clazz) {
        final ExamStreamForm examStreamForm = (ExamStreamForm) clazz.cast(form);
        try {
            validateExamStreamRequest(examStreamForm);
            examStreamManagerService.createOrUpdateExamStreamMapping(examStreamForm);
        } catch (final Exception e) {
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", e, form);
        }
    }

    @Override
    protected <T> void upsertNewRecords(T form, List<Object> failedDataList,
            Class<T> clazz) {
        final ExamStreamForm newStreamForm = (ExamStreamForm) clazz.cast(form);
        ExamStreamEntity response = null;
        String failureMessage = EMPTY_STRING;
        try {
            validateExamStreamRequest(newStreamForm);
            response = examStreamManagerService.createOrUpdateExamStreamMapping(newStreamForm);
        } catch (final Exception e) {
            log.error("Got Exception in examStream import upsertNewRecords for input: {}, exception: ", e, form);
            failureMessage = e.getMessage();
        }

        if (null == response || StringUtils.isNotBlank(failureMessage)) {
            log.error("Error Response for stream : {} in create/update : {}", newStreamForm,
                    response);
            if (EMPTY_STRING.equals(failureMessage)) {
                failureMessage = "Failed to put data in ExamStream collection";
            }
            this.addToFailedList(form,
                    failureMessage, true, EXAM_STREAM_COMPONENT, TYPE, failedDataList);
        }
    }

    private void validateExamStreamRequest(ExamStreamForm examStreamForm) {
        if (Objects.isNull(examStreamForm.getExamId()) || examStreamForm.getExamId() <= 0) {
            throw new BadRequestException(INVALID_EXAM_ID_FOR_EXAM_NAME,
                    INVALID_EXAM_ID_FOR_EXAM_NAME.getExternalMessage(),
                    new Object[] {examStreamForm.getExamFullName()});
        }

        if (StringUtils.isBlank(examStreamForm.getDomains())) {
            throw new BadRequestException(INVALID_PAYTM_STREAM,
                    INVALID_PAYTM_STREAM.getExternalMessage(),
                    new Object[] {examStreamForm.getDomains()});
        }
        examStreamForm.setDomains(examStreamForm.getDomains().trim());
    }
}
