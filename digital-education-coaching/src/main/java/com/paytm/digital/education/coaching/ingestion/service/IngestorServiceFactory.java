package com.paytm.digital.education.coaching.ingestion.service;

import com.paytm.digital.education.coaching.enums.IngestionFormEntity;
import com.paytm.digital.education.coaching.ingestion.service.impl.CoachingBannerIngestorService;
import com.paytm.digital.education.coaching.ingestion.service.impl.CoachingCenterIngestorService;
import com.paytm.digital.education.coaching.ingestion.service.impl.CoachingCourseFeatureIngestorService;
import com.paytm.digital.education.coaching.ingestion.service.impl.CoachingCourseIngestorService;
import com.paytm.digital.education.coaching.ingestion.service.impl.CoachingExamIngestorService;
import com.paytm.digital.education.coaching.ingestion.service.impl.CoachingInstituteIngestorService;
import com.paytm.digital.education.coaching.ingestion.service.impl.CompetitiveExamIngestorService;
import com.paytm.digital.education.coaching.ingestion.service.impl.StreamIngestorService;
import com.paytm.digital.education.coaching.ingestion.service.impl.TopRankerIngestorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class IngestorServiceFactory {

    private final CoachingBannerIngestorService        coachingBannerIngestorService;
    private final CoachingCenterIngestorService        coachingCenterIngestorService;
    private final CoachingCourseFeatureIngestorService coachingCourseFeatureIngestorService;
    private final CoachingCourseIngestorService        coachingCourseIngestorService;
    private final CoachingExamIngestorService          coachingExamIngestorService;
    private final CoachingInstituteIngestorService     coachingInstituteIngestorService;
    private final CompetitiveExamIngestorService       competitiveExamIngestorService;
    private final StreamIngestorService                streamIngestorService;
    private final TopRankerIngestorService             topRankerIngestorService;

    public IngestorService getIngestorService(final IngestionFormEntity formEntity) {

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
            case STREAM_FORM: {
                return this.streamIngestorService;
            }
            case TOP_RANKER_FORM: {
                return this.topRankerIngestorService;
            }
            default: {

            }
        }

        log.error("Got non existent form entity type, entity: {}", formEntity);
        return null;
    }
}
