package com.paytm.digital.education.coaching.ingestion.service.exportdata.impl;

import com.paytm.digital.education.coaching.db.dao.CoachingInstituteDAO;
import com.paytm.digital.education.coaching.ingestion.model.ExportResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingInstituteForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataExportPropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataExportPropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.AbstractExportService;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.ExportService;
import com.paytm.digital.education.coaching.ingestion.transformer.exportdata.ExportCoachingInstituteTransformer;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.GoogleSheetExportConstants.COACHING_INSTITUTE_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.GoogleSheetExportConstants.COACHING_INSTITUTE_SHEET_RANGE;

@Slf4j
@Service
public class CoachingInstituteExportService extends AbstractExportService implements ExportService {

    @Autowired
    private CoachingInstituteDAO coachingInstituteDAO;

    @Override
    public ExportResponse export() {

        final DataExportPropertiesResponse properties = super.getProperties(
                DataExportPropertiesRequest.builder()
                        .sheetIdKey(COACHING_INSTITUTE_SHEET_ID)
                        .sheetRangeKey(COACHING_INSTITUTE_SHEET_RANGE)
                        .build());
        if (null == properties) {
            return ExportResponse.builder().countOfRecordsWritten(0).build();
        }

        final List<CoachingInstituteEntity> entityList = this.coachingInstituteDAO.findAll();
        final List<CoachingInstituteForm> formList =
                ExportCoachingInstituteTransformer.convert(entityList);

        boolean successful = super.processRecords(formList, CoachingInstituteForm.class,
                properties.getSheetId(), properties.getRange());

        if (successful) {
            return ExportResponse.builder().countOfRecordsWritten(formList.size()).build();
        }
        return ExportResponse.builder().countOfRecordsWritten(0).build();
    }
}
