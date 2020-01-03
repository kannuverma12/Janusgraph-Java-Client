package com.paytm.digital.education.coaching.ingestion.service.exportdata.impl;

import com.paytm.digital.education.coaching.ingestion.model.ExportResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CompetitiveExamForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataExportPropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataExportPropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.AbstractExportService;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.ExportService;
import com.paytm.digital.education.coaching.ingestion.transformer.exportdata.ExportCompetitiveExamTransformer;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.paytm.digital.education.coaching.constants.GoogleSheetExportConstants.COMPETITIVE_EXAM_SHEET_ID;

@Service
public class CompetitiveExamExportService extends AbstractExportService implements ExportService {

    private static final Logger log = LoggerFactory.getLogger(CompetitiveExamExportService.class);

    private static final List<String> FIELDS = new ArrayList<String>() {
        {
            add("exam_id");
            add("stream_ids");
            add("priority");
        }
    };

    @Autowired
    private CommonMongoRepository commonMongoRepository;

    @Override
    public ExportResponse export() {
        final DataExportPropertiesResponse properties = super.getProperties(
                DataExportPropertiesRequest.builder()
                        .sheetIdKey(COMPETITIVE_EXAM_SHEET_ID)
                        .build());
        if (null == properties) {
            return ExportResponse.builder().countOfRecordsWritten(0).build();
        }

        final List<Exam> entityList = this.commonMongoRepository.getEntityFieldsForCollection(
                Exam.class, FIELDS);
        final List<CompetitiveExamForm> formList = ExportCompetitiveExamTransformer.convert(
                entityList);

        final int recordsWritten = super.processRecords(formList, CompetitiveExamForm.class,
                properties.getSheetId());

        return ExportResponse.builder()
                .countOfRecordsPresentInDb(entityList.size())
                .countOfRecordsWritten(recordsWritten)
                .build();
    }
}

