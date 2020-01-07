package com.paytm.digital.education.application;

import com.paytm.digital.education.application.constant.Constant;
import com.paytm.digital.education.logging.LoggableDispatcherServlet;
import com.paytm.digital.education.utility.CustomKeyGenerator;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import com.paytm.vault.VaultUtil;
import org.apache.logging.log4j.core.lookup.MainMapLookup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.DispatcherServlet;
import paytm.auth.personaaclclient.config.EnableACLPersonaAuth;

@EnableRetry
@SpringBootApplication(exclude = {EmbeddedMongoAutoConfiguration.class})
@EnableCaching(proxyTargetClass = true)
@EnableACLPersonaAuth
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = Constant.EDUCATION_BASE_PACKAGE)
@ComponentScan({Constant.EDUCATION_BASE_PACKAGE, Constant.PERSONA_APPLICATION_PACKAGE})
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    static {
        MainMapLookup.setMainArguments("digital-education-service");
    }


    static class FetchVault implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
        @Override
        public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
            Boolean isVaultSource = event.getEnvironment()
                    .getProperty("vault.source", Boolean.class, Boolean.FALSE);
            log.info("isVaultSource : {}", isVaultSource);
            if (isVaultSource) {
                String profile = event.getEnvironment().getProperty("spring.profiles.active");
                log.info("Profile : {}", profile);
                if (StringUtils.isEmpty(profile)) {
                    log.error("Profile not present in application.properties");
                    System.exit(1);
                }
                System.setProperty("spring.profiles.active", profile);
                log.info("Going to load vault properties");
                try {
                    loadVaultProperties();
                } catch (Exception e) {
                    log.error("Exception while loading vault properties", e);
                    System.exit(1);
                }
            }
        }
    }

    public static void main(String[] args) {

        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.addListeners(new FetchVault());
        springApplication.run(args);
    }

    private static void loadVaultProperties() throws Exception {
        try {
            VaultUtil.injectVaultProperties("spring.profiles.active");
        } catch (Exception e) {
            log.error("Could not inject vault properties ", e);
            throw e;
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
