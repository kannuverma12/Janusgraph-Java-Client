package com.paytm.digital.education.coaching.ingestion.service.exportdata.impl;

import com.paytm.digital.education.coaching.ingestion.model.ExportResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingBannerForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataExportPropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataExportPropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.AbstractExportService;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.ExportService;
import com.paytm.digital.education.coaching.ingestion.transformer.exportdata.ExportCoachingBannerTransformer;
import com.paytm.digital.education.database.dao.CoachingBannerDAO;
import com.paytm.digital.education.database.entity.CoachingBannerEntity;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.GoogleSheetExportConstants.COACHING_BANNER_SHEET_ID;

@Service
public class CoachingBannerExportService extends AbstractExportService implements ExportService {

    private static final Logger log = LoggerFactory.getLogger(CoachingBannerExportService.class);

    @Autowired
    private CoachingBannerDAO coachingBannerDAO;

    @Override
    public ExportResponse export() {

        final DataExportPropertiesResponse properties = super.getProperties(
                DataExportPropertiesRequest.builder()
                        .sheetIdKey(COACHING_BANNER_SHEET_ID)
                        .build());
        if (null == properties) {
            return ExportResponse.builder().countOfRecordsWritten(0).build();
        }

        final List<CoachingBannerEntity> entityList = this.coachingBannerDAO.findAll();
        final List<CoachingBannerForm> formList = ExportCoachingBannerTransformer.convert(
                entityList);

        final int recordsWritten = super.processRecords(formList, CoachingBannerForm.class,
                properties.getSheetId());

        return ExportResponse.builder()
                .countOfRecordsPresentInDb(entityList.size())
                .countOfRecordsWritten(recordsWritten)
                .build();
    }
}
