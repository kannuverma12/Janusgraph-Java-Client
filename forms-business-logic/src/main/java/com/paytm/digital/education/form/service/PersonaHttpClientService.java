package com.paytm.digital.education.form.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import paytm.auth.personaaclclient.authorized_request_handler.PersonaRestTemplate;

/**
 * A service class providing utility function to make a call to any url which accepts persona
 * authentication. It uses PersonaRestTemplate to send http request
 *
 */
@Service
public class PersonaHttpClientService {

    @Autowired
    @Qualifier("personaTemplateQS")
    private PersonaRestTemplate personaTemplateQS;

    @Autowired
    @Qualifier("personaTemplateHeaders")
    private PersonaRestTemplate personaTemplateHeaders;

    public <T> ResponseEntity<T> makeHttpRequest(
            String url,
            HttpMethod httpMethod,
            MultiValueMap<String, String> headers,
            MultiValueMap<String, String> queryParameters,
            Object body,
            Class<T> returnType) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(url)
                .queryParams(queryParameters);

        HttpEntity<Object> httpEntity = new HttpEntity<Object>(body, headers);
        return personaTemplateQS.exchange(builder.toUriString(), httpMethod, httpEntity,
                returnType);
    }

    public <T> ResponseEntity<T> makeHttpRequestUsingAuthTokenInHeaders(
            String url,
            HttpMethod httpMethod,
            MultiValueMap<String, String> headers,
            MultiValueMap<String, String> queryParameters,
            Object body,
            Class<T> returnType) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(url)
                .queryParams(queryParameters);

        HttpEntity<Object> httpEntity = new HttpEntity<Object>(body, headers);
        return personaTemplateHeaders.exchange(builder.toUriString(), httpMethod, httpEntity,
                returnType);
    }
}

