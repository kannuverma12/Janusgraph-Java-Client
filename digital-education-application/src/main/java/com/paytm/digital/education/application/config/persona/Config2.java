package com.paytm.digital.education.application.config.persona;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import paytm.auth.personaaclclient.authorized_request_handler.PersonaRestTemplate;
import paytm.auth.personaaclclient.enums.AuthTokenLocation;
import paytm.auth.personaaclclient.enums.StoreAuthtokenIn;
import paytm.auth.personaaclclient.models.PersonaAccount;
import paytm.auth.personaaclclient.models.RedisOptions;
import paytm.auth.personaaclclient.models.RequestOptions;

/**
 * Persona RestTemplate Config Code taken from facilities-preorder-services repo,
 * com.paytm.facilities.preorderservices.config.PersonaRestTemplateConfig
 */
@Configuration
public class Config2 {

    @Autowired
    Environment env;

    @Bean
    public RequestOptions requestOptionsHeaders() {
        return new RequestOptions(AuthTokenLocation.HEADERS, "authtoken");
    }

    @Bean
    public RedisOptions redisOptions() {
        String authTokenKey = env.getProperty("redis.authtoken.key");
        return new RedisOptions(null, authTokenKey, StoreAuthtokenIn.MEMORY);

    }

    @Bean(name = "personaRestTemplateQS")
    public PersonaRestTemplate personaRestTemplate(
            @Qualifier("requestOptionsQS") RequestOptions ro,
            @Qualifier("personaAccount") PersonaAccount pa) {
        return new PersonaRestTemplate(ro, pa, redisOptions());
    }

    @Bean(name = "personaRestTemplateHeaders")
    public PersonaRestTemplate personaRestTemplateQS(
            @Qualifier("requestOptionsHeaders") RequestOptions ro,
            @Qualifier("personaAccount") PersonaAccount pa) {
        return new PersonaRestTemplate(ro, pa, redisOptions());
    }
}
