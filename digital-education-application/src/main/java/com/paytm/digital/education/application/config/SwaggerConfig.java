package com.paytm.digital.education.application.config;

import com.paytm.digital.education.application.constant.Constant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String SWAGGER_API_INFO_DESCRIPTION      = "Digital Education";
    private static final String SWAGGER_API_INFO_TITLE            = "SpringBoot Application Digital Education";
    private static final String SWAGGER_API_INFO_VERSION          = "0.0.1";
    private static final String SWAGGER_API_INFO_TERMS_OF_SERVICE = "NA";
    private static final String SWAGGER_API_INFO_CONTACT_NAME     = "Digital Education Gurgaon";
    private static final String SWAGGER_API_INFO_CONTACT_EMAIL    = "education.tech@paytm.com";
    private static final String SWAGGER_API_INFO_LICENSE          = "Apache License Version 2.0";
    private static final String SWAGGER_API_INFO_LICENSE_URL      = "https://www.apache.org/licenses/LICENSE-2.0";
    private static final String SWAGGER_API_INFO_CONTACT_URL      =
            "https://wiki.mypaytm.com/display/ED/Explore+Colleges";

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(Constant.EDUCATION_BASE_PACKAGE))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(metaData())
                .globalOperationParameters(Collections.singletonList(new ParameterBuilder()
                        .name("X-CLIENT-ID")
                        .description("X-CLIENT-ID header")
                        .modelRef(new ModelRef("string"))
                        .parameterType("header")
                        .required(true)
                        .build()));
    }

    private ApiInfo metaData() {
        return new ApiInfo(
                SWAGGER_API_INFO_TITLE,
                SWAGGER_API_INFO_DESCRIPTION,
                SWAGGER_API_INFO_VERSION,
                SWAGGER_API_INFO_TERMS_OF_SERVICE,
                new Contact(SWAGGER_API_INFO_CONTACT_NAME,
                        SWAGGER_API_INFO_CONTACT_URL,
                        SWAGGER_API_INFO_CONTACT_EMAIL),
                SWAGGER_API_INFO_LICENSE,
                SWAGGER_API_INFO_LICENSE_URL, new ArrayList<>());
    }
}
