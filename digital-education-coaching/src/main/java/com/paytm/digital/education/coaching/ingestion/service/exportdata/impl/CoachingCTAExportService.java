package com.paytm.digital.education.coaching.ingestion.service.exportdata.impl;

import com.paytm.digital.education.coaching.db.dao.CoachingCtaDAO;
import com.paytm.digital.education.coaching.ingestion.model.ExportResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCTAForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataExportPropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataExportPropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.AbstractExportService;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.ExportService;
import com.paytm.digital.education.coaching.ingestion.transformer.exportdata.ExportCoachingCTATransformer;
import com.paytm.digital.education.database.entity.CoachingCtaEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.GoogleSheetExportConstants.COACHING_CTA_SHEET_ID;

@Slf4j
@Service
public class CoachingCTAExportService extends AbstractExportService implements ExportService {

    @Autowired
    private CoachingCtaDAO coachingCtaDAO;

    @Override
    public ExportResponse export() {

        final DataExportPropertiesResponse properties = super.getProperties(
                DataExportPropertiesRequest.builder()
                        .sheetIdKey(COACHING_CTA_SHEET_ID)
                        .build());
        if (null == properties) {
            return ExportResponse.builder().countOfRecordsWritten(0).build();
        }

        final List<CoachingCtaEntity> entityList = this.coachingCtaDAO.findAll();
        final List<CoachingCTAForm> formList =
                ExportCoachingCTATransformer.convert(entityList);

        final int recordsWritten = super.processRecords(formList, CoachingCTAForm.class,
                properties.getSheetId());

        return ExportResponse.builder()
                .countOfRecordsPresentInDb(entityList.size())
                .countOfRecordsWritten(recordsWritten)
                .build();
    }
}

