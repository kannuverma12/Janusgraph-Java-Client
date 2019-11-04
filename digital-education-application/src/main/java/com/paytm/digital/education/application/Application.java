package com.paytm.digital.education.application;

import com.paytm.digital.education.application.constant.Constant;
import org.apache.logging.log4j.core.lookup.MainMapLookup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import paytm.auth.personaaclclient.config.EnableACLPersonaAuth;

@SpringBootApplication
@EnableCaching
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

}
