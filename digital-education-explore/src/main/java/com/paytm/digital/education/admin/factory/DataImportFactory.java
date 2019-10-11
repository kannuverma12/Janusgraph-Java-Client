package com.paytm.digital.education.admin.factory;

import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_DATA_INGESTION_ENTITY;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.ingestion.model.IngestionFormEntity;
import com.paytm.digital.education.ingestion.service.importdata.ExamStreamImportService;
import com.paytm.digital.education.ingestion.service.importdata.ImportService;
import com.paytm.digital.education.ingestion.service.importdata.MerchantStreamImportService;
import com.paytm.digital.education.ingestion.service.importdata.StreamDataImportService;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataImportFactory {

    private static Logger log = LoggerFactory.getLogger(DataImportFactory.class);

    private final StreamDataImportService streamDataImportService;
    private final ExamStreamImportService examStreamImportService;
    private final MerchantStreamImportService merchantStreamImportService;

    public ImportService getIngestorService(final IngestionFormEntity formEntity) {
        switch (formEntity) {
            case STREAM_FORM:
                return streamDataImportService;
            case EXAM_STREAM_MAPPING:
                return examStreamImportService;
            case MERCHANT_STREAM:
                return merchantStreamImportService;
            default:
                log.error("Requested entity : {} not supported.", formEntity);
                throw new BadRequestException(INVALID_DATA_INGESTION_ENTITY,
                        INVALID_DATA_INGESTION_ENTITY.getExternalMessage(),
                        new Object[] {formEntity});
        }
    }
}
