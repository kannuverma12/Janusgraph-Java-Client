package com.paytm.digital.education.application.config.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final List<String> allowedHosts;
    private final List<String> allowedMethods;
    private final List<String> allowedHeaders;

    public SecurityConfig(
            @Value("#{'${cors.allowed.hosts}'.split(',')}") final List<String> allowedHosts,
            @Value("#{'${cors.allowed.methods}'.split(',')}") final List<String> allowedMethods,
            @Value("#{'${cors.allowed.headers}'.split(',')}") final List<String> allowedHeaders) {
        this.allowedHosts = allowedHosts;
        this.allowedMethods = allowedMethods;
        this.allowedHeaders = allowedHeaders;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        List<String> allowedHostList = allowedHosts.stream()
                .map(StringUtils::trim)
                .filter(StringUtils::isNotBlank)
                .flatMap(s -> Stream.of("http://" + s, "https://" + s))
                .collect(Collectors.toList());

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedHostList);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(allowedHeaders);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/explore/**", configuration);
        return source;
    }
}
