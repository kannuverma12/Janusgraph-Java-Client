package com.paytm.digital.education.application.config.persona;

import com.paytm.digital.education.application.constant.Constant;
import com.paytm.digital.education.application.constant.ErrorCode;
import com.paytm.digital.education.application.exception.GlobalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import paytm.auth.personaaclclient.authorized_request_handler.PersonaRestTemplate;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;

@Component
public class PersonaRestTemplateFactory {

    @Autowired
    @Qualifier(value = Constant.SAMPLE_API)
    private PersonaRestTemplate sampleApiRestTemplate;

    private Map<String, PersonaRestTemplate> templateMap;

    @PostConstruct
    public void init() {
        templateMap = new HashMap<>();
        templateMap.put(Constant.SAMPLE_API, sampleApiRestTemplate);
    }

    public PersonaRestTemplate getPersonaTemplate(String serviceName) {
        if (!templateMap.containsKey(serviceName)) {
            throw new GlobalException(null,
                    null, ErrorCode.SAE_PERSONA_TEMPLATE_NOT_FOUND,
                    String.format("Persona template for %s not found", serviceName));
        }
        return templateMap.get(serviceName);
    }
}
