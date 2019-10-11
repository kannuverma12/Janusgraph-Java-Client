package com.paytm.digital.education.admin.factory;

import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_DATA_INGESTION_ENTITY;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.ingestion.model.IngestionFormEntity;
import com.paytm.digital.education.ingestion.service.exportdata.ExportService;
import com.paytm.digital.education.ingestion.service.exportdata.StreamDataExportService;
import com.paytm.digital.education.ingestion.service.importdata.StreamDataImportService;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataExportFactory {

    private static Logger log = LoggerFactory.getLogger(DataExportFactory.class);

    private final StreamDataExportService streamDataExportService;

    public ExportService getExportService(final IngestionFormEntity formEntity) {
        switch (formEntity) {
            case STREAM_FORM:
                return streamDataExportService;
            default:
                log.error("Requested entity : {} not supported.", formEntity);
                throw new BadRequestException(INVALID_DATA_INGESTION_ENTITY,
                        INVALID_DATA_INGESTION_ENTITY.getExternalMessage(),
                        new Object[] {formEntity});
        }
    }

}
