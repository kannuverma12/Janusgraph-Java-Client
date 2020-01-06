package com.paytm.digital.education.application;

import com.paytm.digital.education.application.constant.Constant;
import com.paytm.digital.education.logging.LoggableDispatcherServlet;
import com.paytm.digital.education.utility.CustomKeyGenerator;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import com.paytm.vault.VaultUtil;
import org.apache.logging.log4j.core.lookup.MainMapLookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.servlet.DispatcherServlet;
import paytm.auth.personaaclclient.config.EnableACLPersonaAuth;

import java.util.Arrays;
import java.util.List;

@EnableRetry
@SpringBootApplication(exclude = {EmbeddedMongoAutoConfiguration.class})
@EnableCaching(proxyTargetClass = true)
@EnableACLPersonaAuth
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = Constant.EDUCATION_BASE_PACKAGE)
@ComponentScan({Constant.EDUCATION_BASE_PACKAGE, Constant.PERSONA_APPLICATION_PACKAGE})
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    private Environment env;

    static {
        MainMapLookup.setMainArguments("digital-education-service");
    }

    public static void main(String[] args) throws Exception {

        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        List<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains("staging") || activeProfiles.contains("production")) {
            try {
                VaultUtil.injectVaultProperties("spring.profiles.active");
            } catch (Exception e) {
                log.error("Could not inject vault properties ", e);
                throw e;
            }
        }
    }

    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet() {
        return new LoggableDispatcherServlet();
    }

    @Bean("customKeyGenerator")
    public KeyGenerator keyGenerator() {
        return new CustomKeyGenerator();
    }
}
