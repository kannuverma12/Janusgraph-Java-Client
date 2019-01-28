package com.paytm.digital.education.application.config.persona;

import com.paytm.digital.education.application.config.metric.DataDogClient;
import com.paytm.digital.education.application.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import paytm.auth.personaaclclient.authorized_request_handler.PersonaRestTemplate;
import paytm.auth.personaaclclient.enums.AuthTokenLocation;
import paytm.auth.personaaclclient.enums.StoreAuthtokenIn;
import paytm.auth.personaaclclient.models.PersonaAccount;
import paytm.auth.personaaclclient.models.RedisOptions;
import paytm.auth.personaaclclient.models.RequestOptions;

@Configuration
public class PersonaRestTemplateConfig {

    private static final String REDIS_MEMORY_KEY = "SEED_SERVICE_AUTH_TOKEN";

    @Value("${sampleApi.readTimeout}")
    private Integer             sampleApiReadTimeout;
    @Value("${sampleApi.connectTimeout}")
    private Integer             sampleApiConnectionTimeout;

    @Autowired
    private Environment env;

    @Autowired
    private DataDogClient dataDogClient;

    @Bean
    public RequestOptions requestOptionsQS() {
        return new RequestOptions(AuthTokenLocation.QS, "authtoken");
    }

    @Bean
    public PersonaAccount personaAccount() {
        return new PersonaAccount(
                env.getProperty("persona.account.username"),
                env.getProperty("persona.account.password"),
                env.getProperty("persona.account.clientId"),
                env.getProperty("persona.account.clientSecret"),
                env.getProperty("persona.account.notRedirect", Boolean.class),
                env.getProperty("persona.account.responseType"),
                env.getProperty("persona.account.grantType"));
    }

    @Bean
    public RedisOptions redisOptionsMemory() {
        return new RedisOptions(null, REDIS_MEMORY_KEY, StoreAuthtokenIn.MEMORY);
    }

    @Bean(name = Constant.SAMPLE_API)
    public PersonaRestTemplate sampleApiPersonaRestTemplateQS(
            final RequestOptions requestOptions,
            final PersonaAccount personaAccount,
            final RedisOptions redisOptions) {

        PersonaRestTemplate restTemplate = new PersonaRestTemplate(requestOptions,
                personaAccount, redisOptions, dataDogClient.getStatsDClient());

        restTemplate.setRequestFactory(this.buildHttpRequestFactoryForRestTemplate(
                sampleApiReadTimeout, sampleApiConnectionTimeout));

        return restTemplate;
    }

    private HttpComponentsClientHttpRequestFactory buildHttpRequestFactoryForRestTemplate(
            final int readTimeout,
            final int connectTimeout) {

        final HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory();
        httpComponentsClientHttpRequestFactory.setReadTimeout(readTimeout);
        httpComponentsClientHttpRequestFactory.setConnectTimeout(connectTimeout);
        return httpComponentsClientHttpRequestFactory;
    }
}
