package com.paytm.digital.education.coaching.ingestion.service.impl;

import com.paytm.digital.education.coaching.exeptions.CoachingBaseException;
import com.paytm.digital.education.coaching.http.HttpConstants;
import com.paytm.digital.education.coaching.http.HttpRequestDetails;
import com.paytm.digital.education.coaching.ingestion.model.IngestorResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingBannerForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.IngestorService;
import com.paytm.digital.education.coaching.ingestion.service.IngestorServiceHelper;
import com.paytm.digital.education.coaching.ingestion.transformer.IngestorCoachingBannerTransformer;
import com.paytm.digital.education.coaching.producer.model.dto.CoachingBannerDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingBannerDataRequest;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_BANNER_SHEET_HEADER_RANGE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_BANNER_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_BANNER_SHEET_RANGE_TEMPLATE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_BANNER_SHEET_START_ROW;

@Slf4j
@Service
public class CoachingBannerIngestorService extends IngestorServiceHelper
        implements IngestorService {

    private static final String TYPE = "CoachingBanner";

    @Value("${coaching.producer.bannerApi.url}")
    private String producerBannerApiUrl;

    @Override
    public IngestorResponse ingest() {

        final PropertiesResponse propertiesResponse = this.getProperties(PropertiesRequest.builder()
                .sheetIdKey(COACHING_BANNER_SHEET_ID)
                .sheetHeaderRangeKey(COACHING_BANNER_SHEET_HEADER_RANGE)
                .sheetStartRowKey(COACHING_BANNER_SHEET_START_ROW)
                .sheetRangeTemplateKey(COACHING_BANNER_SHEET_RANGE_TEMPLATE)
                .build());

        final List<Object> bannerFormData = this.getFormData(propertiesResponse);

        final List<CoachingBannerForm> failedBannerFormList = this.getFailedData(
                TYPE, CoachingBannerForm.class, COACHING);

        return this.processRecords(bannerFormData,
                failedBannerFormList, CoachingBannerForm.class, TYPE
        );
    }

    @Override
    protected <T> void upsertNewRecords(final T form, final List<Object> failedDataList,
            final Class<T> clazz) {

        final CoachingBannerForm bannerForm = (CoachingBannerForm) clazz.cast(form);

        ResponseEntity<CoachingBannerDTO> response = null;
        String failureMessage = EMPTY_STRING;
        try {
            if (null == bannerForm.getId()) {
                response = this.makeCall(IngestorCoachingBannerTransformer.convert(bannerForm),
                        HttpMethod.POST);
            } else {
                response = this.makeCall(IngestorCoachingBannerTransformer.convert(bannerForm),
                        HttpMethod.PUT);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertNewRecords for input: {}, exception: ", form, e);
            failureMessage = e.getMessage();
        }

        if (null == response || !response.getStatusCode().is2xxSuccessful()
                || null == response.getBody() || null == response.getBody().getCoachingBannerId()) {

            if (EMPTY_STRING.equals(failureMessage)) {
                failureMessage = "Failed to put new data in CoachingBanner collection";
            }

            this.addToFailedList(JsonUtils.toJson(form),
                    failureMessage, true, COACHING, TYPE, failedDataList);
        }
    }

    @Override
    protected <T> void upsertFailedRecords(final T form, final Class<T> clazz) {
        final CoachingBannerForm bannerForm = (CoachingBannerForm) clazz.cast(form);
        try {
            if (null == bannerForm.getId()) {
                this.makeCall(IngestorCoachingBannerTransformer.convert(bannerForm),
                        HttpMethod.POST);
            } else {
                this.makeCall(IngestorCoachingBannerTransformer.convert(bannerForm),
                        HttpMethod.PUT);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", form, e);
        }
    }

    private ResponseEntity<CoachingBannerDTO> makeCall(final CoachingBannerDataRequest request,
            final HttpMethod method) throws CoachingBaseException {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        final HttpRequestDetails requestDetails = HttpRequestDetails.builder()
                .requestMethod(method)
                .url(this.producerBannerApiUrl)
                .body(request)
                .headers(httpHeaders)
                .requestApiName("CoachingBannerIngestorService")
                .build();

        return this.httpUtil.exchange(this.restTemplateFactory.getRestTemplate(
                HttpConstants.GENERIC_HTTP_SERVICE), requestDetails, CoachingBannerDTO.class);
    }
}
