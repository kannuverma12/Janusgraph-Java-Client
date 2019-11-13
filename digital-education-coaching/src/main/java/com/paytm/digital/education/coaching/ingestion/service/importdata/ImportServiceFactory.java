package com.paytm.digital.education.coaching.ingestion.service.importdata;

import com.paytm.digital.education.coaching.ingestion.model.IngestionFormEntity;
import com.paytm.digital.education.coaching.ingestion.service.importdata.impl.CoachingBannerImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.impl.CoachingCTAImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.impl.CoachingCTAMappingImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.impl.CoachingCenterImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.impl.CoachingCourseFeatureImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.impl.CoachingCourseImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.impl.CoachingExamImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.impl.CoachingInstituteImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.impl.CompetitiveExamImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.impl.TopRankerImportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ImportServiceFactory {

    private final CoachingBannerImportService        coachingBannerIngestorService;
    private final CoachingCenterImportService        coachingCenterIngestorService;
    private final CoachingCourseFeatureImportService coachingCourseFeatureIngestorService;
    private final CoachingCourseImportService     coachingCourseIngestorService;
    private final CoachingExamImportService       coachingExamIngestorService;
    private final CoachingInstituteImportService  coachingInstituteIngestorService;
    private final CompetitiveExamImportService    competitiveExamIngestorService;
    private final TopRankerImportService          topRankerIngestorService;
    private final CoachingCTAImportService        ctaImportService;
    private final CoachingCTAMappingImportService coachingCTAMappingImportService;


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
            case COACHING_CTA_MAPPING_FORM: {
                return this.coachingCTAMappingImportService;
            }
            default: {

            }
        }

        log.error("Got non existent form entity type, entity: {}", formEntity);
        return null;
    }
}
