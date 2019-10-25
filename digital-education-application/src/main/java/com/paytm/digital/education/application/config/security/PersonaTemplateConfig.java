package com.paytm.digital.education.application.config.security;


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
public class PersonaTemplateConfig {

    private static final String REDIS_MEMORY_KEY = "SEED_SERVICE_AUTH_TOKEN";

    @Autowired
    Environment env;

    @Bean
    public RequestOptions restRequestOptionsQS() {
        return new RequestOptions(AuthTokenLocation.QS, "authtoken");
    }

    @Bean
    public RequestOptions restRequestOptionsHeaders() {
        return new RequestOptions(AuthTokenLocation.HEADERS, "authtoken");
    }

    @Bean
    PersonaAccount restPersonaAccount() {
        String userName = env.getProperty("persona.account.username");
        String password = env.getProperty("persona.account.password");
        String clientId = env.getProperty("persona.account.clientId");
        String clientSecret = env.getProperty("persona.account.clientSecret");
        Boolean notRedirect = env.getProperty("persona.account.notRedirect", Boolean.class);
        String responseType = env.getProperty("persona.account.responseType");
        String grantType = env.getProperty("persona.account.grantType");
        return new PersonaAccount(userName, password, clientId, clientSecret,
                notRedirect.booleanValue(), responseType, grantType);
    }

    @Bean(name = "personaRedisOptions")
    public RedisOptions redisOptions() {
        return new RedisOptions(null, REDIS_MEMORY_KEY, StoreAuthtokenIn.MEMORY);

    }

    @Bean(name = "personaTemplateQS")
    public PersonaRestTemplate personaTemplateQS(
            @Qualifier("restRequestOptionsQS") RequestOptions ro,
            @Qualifier("restPersonaAccount") PersonaAccount pa) {
        return new PersonaRestTemplate(ro, pa, redisOptions());
    }

    @Bean(name = "personaTemplateHeaders")
    public PersonaRestTemplate personaTemplate(
            @Qualifier("restRequestOptionsHeaders") RequestOptions ro,
            @Qualifier("restPersonaAccount") PersonaAccount pa) {
        return new PersonaRestTemplate(ro, pa, redisOptions());
    }
}
