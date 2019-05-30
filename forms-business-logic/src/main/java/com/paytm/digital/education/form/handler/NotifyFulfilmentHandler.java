package com.paytm.digital.education.form.handler;

import com.paytm.digital.education.form.request.FulfilmentKafkaObject;
import com.paytm.digital.education.form.request.FulfilmentKafkaPostDataObject;
import com.paytm.digital.education.form.service.PersonaHttpClientService;
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

    @Override
    public void handle(FulfilmentKafkaObject fulfilmentKafkaObject) {
        try {
            String url = fulfilmentKafkaObject.getUrl();
            FulfilmentKafkaPostDataObject postData = fulfilmentKafkaObject.getPostData();

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

            ResponseEntity<HashMap> responseEntity = personaHttpClientService.makeHttpRequest(
                    url, HttpMethod.POST, headers, null, postData, HashMap.class);

            // todo: failure handling, retries

            log.info("Updated fulfilment :: " + responseEntity.getBody());
        } catch (Exception e) {
            // todo: send metrics
            log.error("Error updating order status to fulfilment", e);
        }
    }
}
