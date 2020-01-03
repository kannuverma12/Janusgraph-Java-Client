package com.paytm.digital.education.coaching.ingestion.service.exportdata.impl;

import com.paytm.digital.education.coaching.ingestion.model.ExportResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCourseForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataExportPropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataExportPropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.AbstractExportService;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.ExportService;
import com.paytm.digital.education.coaching.ingestion.transformer.exportdata.ExportCoachingCourseTransformer;
import com.paytm.digital.education.database.dao.CoachingCourseDAO;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.GoogleSheetExportConstants.COACHING_COURSE_SHEET_ID;

@Service
public class CoachingCourseExportService extends AbstractExportService implements ExportService {

    private static final Logger log = LoggerFactory.getLogger(CoachingCourseExportService.class);

    @Autowired
    private CoachingCourseDAO coachingCourseDAO;

    @Override
    public ExportResponse export() {

        final DataExportPropertiesResponse properties = super.getProperties(
                DataExportPropertiesRequest.builder()
                        .sheetIdKey(COACHING_COURSE_SHEET_ID)
                        .build());
        if (null == properties) {
            return ExportResponse.builder().countOfRecordsWritten(0).build();
        }

        final List<CoachingCourseEntity> entityList = this.coachingCourseDAO.findAll();
        final List<CoachingCourseForm> formList =
                ExportCoachingCourseTransformer.convert(entityList);

        final int recordsWritten = super.processRecords(formList, CoachingCourseForm.class,
                properties.getSheetId());

        return ExportResponse.builder()
                .countOfRecordsPresentInDb(entityList.size())
                .countOfRecordsWritten(recordsWritten)
                .build();
    }
}

