package com.paytm.digital.education.coaching.ingestion.service.importdata;

import com.paytm.digital.education.coaching.ingestion.model.IngestionFormEntity;
import com.paytm.digital.education.coaching.ingestion.service.importdata.impl.CoachingBannerImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.impl.CoachingCTAImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.impl.CoachingCenterImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.impl.CoachingCourseFeatureImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.impl.CoachingCourseImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.impl.CoachingExamImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.impl.CoachingInstituteImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.impl.CompetitiveExamImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.impl.TopRankerImportService;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ImportServiceFactory {

    private static final Logger log = LoggerFactory.getLogger(ImportServiceFactory.class);

    private final CoachingBannerImportService        coachingBannerIngestorService;
    private final CoachingCenterImportService        coachingCenterIngestorService;
    private final CoachingCourseFeatureImportService coachingCourseFeatureIngestorService;
    private final CoachingCourseImportService        coachingCourseIngestorService;
    private final CoachingExamImportService          coachingExamIngestorService;
    private final CoachingInstituteImportService     coachingInstituteIngestorService;
    private final CompetitiveExamImportService       competitiveExamIngestorService;
    private final TopRankerImportService             topRankerIngestorService;
    private final CoachingCTAImportService           ctaImportService;

    public ImportService getIngestorService(final IngestionFormEntity formEntity) {

        switch (formEntity) {
            case COACHING_BANNER_FORM: {
                return this.coachingBannerIngestorService;
            }
            case COACHING_CENTER_FORM: {
                return this.coachingCenterIngestorService;
            }
            case COACHING_COURSE_FEATURE_FORM: {
                return this.coachingCourseFeatureIngestorService;
            }
            case COACHING_COURSE_FORM: {
                return this.coachingCourseIngestorService;
            }
            case COACHING_EXAM_FORM: {
                return this.coachingExamIngestorService;
            }
            case COACHING_INSTITUTE_FORM: {
                return this.coachingInstituteIngestorService;
            }
            case COMPETITIVE_EXAM_FORM: {
                return this.competitiveExamIngestorService;
            }
            case TOP_RANKER_FORM: {
                return this.topRankerIngestorService;
            }
            case COACHING_COURSE_CTA_FORM: {
                return this.ctaImportService;
            }
            default: {

            }
        }

        log.error("Got non existent form entity type, entity: {}", formEntity);
        return null;
    }
}
