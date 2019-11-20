package com.paytm.digital.education.application;

import com.paytm.digital.education.application.constant.Constant;
import com.paytm.digital.education.logging.LoggableDispatcherServlet;
import org.apache.logging.log4j.core.lookup.MainMapLookup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.DispatcherServlet;
import paytm.auth.personaaclclient.config.EnableACLPersonaAuth;

@SpringBootApplication(exclude = {EmbeddedMongoAutoConfiguration.class})
@EnableCaching(proxyTargetClass = true)
@EnableACLPersonaAuth
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = Constant.EDUCATION_BASE_PACKAGE)
@ComponentScan({Constant.EDUCATION_BASE_PACKAGE, Constant.PERSONA_APPLICATION_PACKAGE})
public class Application {

    static {
        MainMapLookup.setMainArguments("digital-education-service");
    }

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }


    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet() {
        return new LoggableDispatcherServlet();
    }
}
