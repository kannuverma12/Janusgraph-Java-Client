package com.paytm.digital.education.config.properties;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
        @PropertySource("classpath:utilities-application.properties")
    })
public class UtilitiesDefaultProperties {
}
