package com.paytm.digital.education.coaching.ingestion.service.exportdata.impl;

import com.paytm.digital.education.coaching.db.dao.CoachingCourseDAO;
import com.paytm.digital.education.coaching.ingestion.model.ExportResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCourseForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataExportPropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataExportPropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.AbstractExportService;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.ExportService;
import com.paytm.digital.education.coaching.ingestion.transformer.exportdata.ExportCoachingCourseTransformer;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.GoogleSheetExportConstants.COACHING_COURSE_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.GoogleSheetExportConstants.COACHING_COURSE_SHEET_RANGE;

@Slf4j
@Service
public class CoachingCourseExportService extends AbstractExportService implements ExportService {

    @Autowired
    private CoachingCourseDAO coachingCourseDAO;

    @Override
    public ExportResponse export() {

        final DataExportPropertiesResponse properties = super.getProperties(
                DataExportPropertiesRequest.builder()
                        .sheetIdKey(COACHING_COURSE_SHEET_ID)
                        .sheetRangeKey(COACHING_COURSE_SHEET_RANGE)
                        .build());
        if (null == properties) {
            return ExportResponse.builder().countOfRecordsWritten(0).build();
        }

        final List<CoachingCourseEntity> entityList = this.coachingCourseDAO.findAll();
        final List<CoachingCourseForm> formList =
                ExportCoachingCourseTransformer.convert(entityList);

        boolean successful = super.processRecords(formList, CoachingCourseForm.class,
                properties.getSheetId(), properties.getRange());

        if (successful) {
            return ExportResponse.builder().countOfRecordsWritten(formList.size()).build();
        }
        return ExportResponse.builder().countOfRecordsWritten(0).build();
    }
}

