package com.paytm.digital.education.ingestion.service.exportdata;

import static com.paytm.digital.education.ingestion.constant.IngestionConstants.STREAM_SHEET_ID;

import com.paytm.digital.education.ingestion.converter.StreamDataConverter;
import com.paytm.digital.education.ingestion.dao.StreamDAO;
import com.paytm.digital.education.ingestion.request.DataExportPropertiesRequest;
import com.paytm.digital.education.ingestion.response.DataExportPropertiesResponse;
import com.paytm.digital.education.ingestion.response.ExportResponse;
import com.paytm.digital.education.ingestion.sheets.StreamForm;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StreamDataExportService extends AbstractExportService implements ExportService {

    private static Logger log = LoggerFactory.getLogger(StreamDataExportService.class);

    @Autowired
    private StreamDAO streamDAO;

    @Override
    public ExportResponse export() {

        final DataExportPropertiesResponse properties = super.getProperties(
                DataExportPropertiesRequest.builder()
                        .sheetIdKey(STREAM_SHEET_ID)
                        .build(), StreamForm.class);

        final List<StreamEntity> entityList = this.streamDAO.findAll();
        final List<StreamForm> formList = StreamDataConverter.convertToFormData(entityList);

        final int recordsWritten = super.processRecords(formList, StreamForm.class,
                properties.getSheetId());

        return ExportResponse.builder()
                .countOfRecordsPresentInDb(entityList.size())
                .countOfRecordsWritten(recordsWritten)
                .build();
    }
}
