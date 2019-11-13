package com.paytm.digital.education.coaching.ingestion.service.exportdata;

import com.paytm.digital.education.coaching.ingestion.model.IngestionFormEntity;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.impl.CoachingBannerExportService;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.impl.CoachingCTAExportService;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.impl.CoachingCenterExportService;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.impl.CoachingCourseExportService;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.impl.CoachingCourseFeatureExportService;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.impl.CoachingExamExportService;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.impl.CoachingInstituteExportService;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.impl.CompetitiveExamExportService;
import com.paytm.digital.education.coaching.ingestion.service.exportdata.impl.TopRankerExportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ExportServiceFactory {

    private final CoachingBannerExportService        coachingBannerExportService;
    private final CoachingCenterExportService        coachingCenterExportService;
    private final CoachingCourseFeatureExportService coachingCourseFeatureExportService;
    private final CoachingCourseExportService        coachingCourseExportService;
    private final CoachingExamExportService          coachingExamExportService;
    private final CoachingInstituteExportService     coachingInstituteExportService;
    private final CompetitiveExamExportService       competitiveExamExportService;
    private final TopRankerExportService             topRankerExportService;
    private final CoachingCTAExportService           coachingCTAExportService;


    public ExportService getExportService(final IngestionFormEntity formEntity) {

        switch (formEntity) {
            case COACHING_BANNER_FORM: {
                return this.coachingBannerExportService;
            }
            case COACHING_CENTER_FORM: {
                return this.coachingCenterExportService;
            }
            case COACHING_COURSE_FEATURE_FORM: {
                return this.coachingCourseFeatureExportService;
            }
            case COACHING_COURSE_FORM: {
                return this.coachingCourseExportService;
            }
            case COACHING_EXAM_FORM: {
                return this.coachingExamExportService;
            }
            case COACHING_INSTITUTE_FORM: {
                return this.coachingInstituteExportService;
            }
            case COMPETITIVE_EXAM_FORM: {
                return this.competitiveExamExportService;
            }
            case TOP_RANKER_FORM: {
                return this.topRankerExportService;
            }
            case COACHING_COURSE_CTA_FORM: {
                return this.coachingCTAExportService;
            }
            default: {
            }
        }

        log.error("Got non existent form entity type, entity: {}", formEntity);
        return null;
    }
}
