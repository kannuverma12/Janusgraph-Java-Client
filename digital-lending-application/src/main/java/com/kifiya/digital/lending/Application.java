package com.kifiya.digital.lending;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
//@EnableCaching(proxyTargetClass = true)
//@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.kifiya.digital.lending")
//@ComponentScan("com.kifiya.digital.lending")
public class Application {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.run(args);
    }

}
