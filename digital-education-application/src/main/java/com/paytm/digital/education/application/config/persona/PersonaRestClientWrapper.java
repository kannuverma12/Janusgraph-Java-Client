package com.paytm.digital.education.application.config.persona;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.paytm.digital.education.application.config.aspect.LogRequestResponse;
import com.paytm.digital.education.application.config.aspect.RequestRate;
import com.paytm.digital.education.application.config.aspect.Timed;
import com.paytm.digital.education.metrics.DataDogClient;
import com.paytm.digital.education.exception.ExceptionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import paytm.auth.personaaclclient.authorized_request_handler.PersonaRestTemplate;

@Service
public class PersonaRestClientWrapper {

    @Autowired
    private DataDogClient dataDogClient;

    @Autowired
    private PersonaRestTemplateFactory personaTemplateFactory;

    @RequestRate
    @Timed(logToDataDog = true)
    @LogRequestResponse(logOnInfoMode = false)
    public <T> ResponseEntity<T> doRestCall(String dependencyName, String url, Class<T> responseType) {
        PersonaRestTemplate personaRestTemplate = personaTemplateFactory.getPersonaTemplate(dependencyName);
        try {
            return personaRestTemplate.getForEntity(url, responseType);
        } catch (HttpClientErrorException re) {
            dataDogClient.recordResponseCodeCount(url.toString(), re.getStatusCode());
            throw ExceptionHelper.buildDependencyException(dependencyName, re.getStatusCode(),
                    re.getResponseBodyAsString(), re);
        }
    }

    @RequestRate
    @Timed(logToDataDog = true)
    @LogRequestResponse(logOnInfoMode = false)
    public <T> ResponseEntity<T> doPostCall(String dependencyName, String url, ObjectNode request,
                                            Class<T> responseType) {
        PersonaRestTemplate personaRestTemplate =
                personaTemplateFactory.getPersonaTemplate(dependencyName);
        try {
            return personaRestTemplate.postForEntity(url, request, responseType);
        } catch (HttpClientErrorException re) {
            dataDogClient.recordResponseCodeCount(url.toString(), re.getStatusCode());
            throw ExceptionHelper.buildDependencyException(dependencyName, re.getStatusCode(),
                    re.getResponseBodyAsString(), re);
        }
    }
}
