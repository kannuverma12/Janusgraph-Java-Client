package com.paytm.digital.education.form.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


public class BaseSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${cors.allowed.methods}")
    private String corsAllowedMethods;

    @Value("${cors.allowed.hosts}")
    private String corsAllowedHosts;

    @Value("${cors.allowed.headers}")
    private String corsAllowedHeaders;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(corsAllowedHosts.split(",")));
        configuration.setAllowedMethods(Arrays.asList(corsAllowedMethods.split(",")));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList(corsAllowedHeaders.split(",")));
        configuration.setMaxAge(3600L);
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
