package com.paytm.digital.education.form.handler;

import com.paytm.digital.education.form.dao.FormDataDao;
import com.paytm.digital.education.form.dao.PaymentPostingErrorDao;
import com.paytm.digital.education.form.request.FulfilmentKafkaObject;
import com.paytm.digital.education.form.request.FulfilmentKafkaPostDataObject;
import com.paytm.digital.education.form.service.PersonaHttpClientService;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;

@Slf4j
@Service
@AllArgsConstructor
public class NotifyFulfilmentHandler extends BaseHandler<FulfilmentKafkaObject> {

    private PersonaHttpClientService personaHttpClientService;

    private FormDataDao formDataDao;

    private PaymentPostingErrorDao paymentPostingErrorDao;

    @Override
    public void handle(FulfilmentKafkaObject fulfilmentKafkaObject) {
        try {
            String url = fulfilmentKafkaObject.getUrl();
            FulfilmentKafkaPostDataObject postData = fulfilmentKafkaObject.getPostData();

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

            ResponseEntity<HashMap> responseEntity = personaHttpClientService.makeHttpRequest(
                    url, HttpMethod.POST, headers, null, postData, HashMap.class);

            log.info("Updated fulfilment :: {} Body: {}",
                    JsonUtils.toJson(fulfilmentKafkaObject), responseEntity.getBody());

            // todo: failure handling, retries

            formDataDao.updateFulfilmentStatus(
                    fulfilmentKafkaObject.getRefId(),
                    Integer.parseInt(postData.getStatus())
            );
        } catch (Exception e) {
            // todo: send metrics
            log.error("Error updating order status to fulfilment:{}",
                    JsonUtils.toJson(fulfilmentKafkaObject), e);
            paymentPostingErrorDao.upsertRecord(fulfilmentKafkaObject.getRefId(),
                    "Error updating order status to fulfilment: " + JsonUtils
                            .toJson(fulfilmentKafkaObject) + "Exception: " + e, null, null);
        }
    }

}
