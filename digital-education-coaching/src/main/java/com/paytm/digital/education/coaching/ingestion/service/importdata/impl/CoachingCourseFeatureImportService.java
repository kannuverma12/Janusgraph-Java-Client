package com.paytm.digital.education.coaching.ingestion.service.importdata.impl;

import com.paytm.digital.education.coaching.exeptions.CoachingBaseException;
import com.paytm.digital.education.coaching.ingestion.model.ImportResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCourseFeatureForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.importdata.AbstractImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.ImportService;
import com.paytm.digital.education.coaching.ingestion.transformer.importdata.ImportCoachingCourseFeatureTransformer;
import com.paytm.digital.education.coaching.producer.controller.ProducerCoachingCourseFeatureController;
import com.paytm.digital.education.coaching.producer.model.dto.CoachingCourseFeatureDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseFeatureDataRequest;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;
import static com.paytm.digital.education.coaching.constants.GoogleSheetImportConstants.COACHING_COURSE_FEATURE_SHEET_ID;

@Slf4j
@Service
public class CoachingCourseFeatureImportService extends AbstractImportService
        implements ImportService {

    private static final String TYPE = "CoachingCourseFeature";

    @Value("${coaching.course.feature.logo.prefix}")
    protected String coachingCourseFeatureLogoPrefix;

    @Autowired
    private ProducerCoachingCourseFeatureController producerCoachingCourseFeatureController;

    @Override
    public ImportResponse ingest() {

        final DataImportPropertiesResponse dataImportPropertiesResponse = this.getProperties(
                CoachingCourseFeatureForm.class,
                DataImportPropertiesRequest.builder()
                        .sheetIdKey(COACHING_COURSE_FEATURE_SHEET_ID)
                        .build());

        final List<Object> courseFeatureData = this.getFormData(dataImportPropertiesResponse);

        final List<CoachingCourseFeatureForm> failedCourseFeatureFormList =
                this.getFailedData(TYPE, CoachingCourseFeatureForm.class, COACHING);

        return this.processRecords(courseFeatureData, failedCourseFeatureFormList,
                CoachingCourseFeatureForm.class, TYPE);
    }

    @Override
    protected <T> void upsertNewRecords(final T form, final List<Object> failedDataList,
            final Class<T> clazz) {

        final CoachingCourseFeatureForm featureForm = (CoachingCourseFeatureForm) clazz.cast(form);

        ResponseEntity<CoachingCourseFeatureDTO> response = null;
        String failureMessage = EMPTY_STRING;
        try {
            final CoachingCourseFeatureDataRequest request = this.buildRequest(featureForm);
            if (null == featureForm.getCourseFacilityId()) {
                response = this.producerCoachingCourseFeatureController.createCoachingCourseFeature(
                        request);
            } else {
                response = this.producerCoachingCourseFeatureController.updateCoachingCourseFeature(
                        request);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertNewRecords for input: {}, exception: ", form, e);
            failureMessage = e.getMessage();
        }
        if (null == response || !response.getStatusCode().is2xxSuccessful()
                || null == response.getBody() || null == response.getBody()
                .getCoachingCourseFeatureId()) {
            log.error("Response: {}", response);
            if (EMPTY_STRING.equals(failureMessage)) {
                failureMessage = "Failed to put new data in CoachingCourseFeature collection";
            }
            this.addToFailedList(JsonUtils.toJson(form),
                    failureMessage, true, COACHING, TYPE, failedDataList);
        }
    }

    @Override
    protected <T> void upsertFailedRecords(T form, Class<T> clazz) {
        final CoachingCourseFeatureForm featureForm = (CoachingCourseFeatureForm) clazz.cast(form);
        try {
            final CoachingCourseFeatureDataRequest request = this.buildRequest(featureForm);
            if (null == featureForm.getInstituteId()) {
                this.producerCoachingCourseFeatureController.createCoachingCourseFeature(request);
            } else {
                this.producerCoachingCourseFeatureController.updateCoachingCourseFeature(request);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", form, e);
        }
    }

    private CoachingCourseFeatureDataRequest buildRequest(final CoachingCourseFeatureForm form)
            throws CoachingBaseException {
        final CoachingCourseFeatureDataRequest request = ImportCoachingCourseFeatureTransformer
                .convert(form);
        request.setLogo(this.uploadFile(request.getLogo(), this.coachingCourseFeatureLogoPrefix));
        return request;
    }
}
