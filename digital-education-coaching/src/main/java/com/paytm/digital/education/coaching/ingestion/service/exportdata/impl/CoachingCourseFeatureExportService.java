package com.paytm.digital.education.coaching.ingestion.service.exportdata.impl;

import com.paytm.digital.education.coaching.ingestion.model.ExportResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCourseFeatureForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataExportPropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataExportPropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.AbstractExportService;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.ExportService;
import com.paytm.digital.education.coaching.ingestion.transformer.exportdata.ExportCoachingCourseFeatureTransformer;
import com.paytm.digital.education.database.dao.CoachingCourseFeatureDAO;
import com.paytm.digital.education.database.entity.CoachingCourseFeatureEntity;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.GoogleSheetExportConstants.COACHING_COURSE_FEATURE_SHEET_ID;

@Service
public class CoachingCourseFeatureExportService extends AbstractExportService
        implements ExportService {

    private static final Logger log =
            LoggerFactory.getLogger(CoachingCourseFeatureExportService.class);

    @Autowired
    private CoachingCourseFeatureDAO coachingCourseFeatureDAO;

    @Override
    public ExportResponse export() {

        final DataExportPropertiesResponse properties = super.getProperties(
                DataExportPropertiesRequest.builder()
                        .sheetIdKey(COACHING_COURSE_FEATURE_SHEET_ID)
                        .build());
        if (null == properties) {
            return ExportResponse.builder().countOfRecordsWritten(0).build();
        }

        final List<CoachingCourseFeatureEntity> entityList =
                this.coachingCourseFeatureDAO.findAll();
        final List<CoachingCourseFeatureForm> formList =
                ExportCoachingCourseFeatureTransformer.convert(entityList);

        final int recordsWritten = super.processRecords(formList, CoachingCourseFeatureForm.class,
                properties.getSheetId());

        return ExportResponse.builder()
                .countOfRecordsPresentInDb(entityList.size())
                .countOfRecordsWritten(recordsWritten)
                .build();
    }
}
