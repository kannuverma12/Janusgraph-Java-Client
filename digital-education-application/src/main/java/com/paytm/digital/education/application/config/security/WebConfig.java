package com.paytm.digital.education.application.config.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.stream.Stream;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String[] exploreAllowedHosts;
    private final String[] exploreAllowedHeaders;
    private final String[] fblAllowedHosts;
    private final String[] fblAllowedHeaders;
    private final String[] allowedMethods;


    public WebConfig(
            @Value("#{'${cors.allowed.hosts}'.split(',')}") final String[] exploreAllowedHosts,
            @Value("#{'${cors.allowed.headers}'.split(',')}") final String[] exploreAllowedHeaders,

            @Value("#{'${fbl.cors.allowed.hosts}'.split(',')}") final String[] fblAllowedHosts,
            @Value("#{'${fbl.cors.allowed.headers}'.split(',')}") final String[] fblAllowedHeaders,

            @Value("#{'${cors.allowed.methods}'.split(',')}") final String[] allowedMethods) {
        this.exploreAllowedHosts = Arrays.stream(exploreAllowedHosts)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotBlank)
                .flatMap(s -> Stream.of("http://" + s, "https://" + s))
                .toArray(String[]::new);
        this.exploreAllowedHeaders = exploreAllowedHeaders;

        this.fblAllowedHosts = Arrays.stream(fblAllowedHosts)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotBlank)
                .flatMap(s -> Stream.of("http://" + s, "https://" + s))
                .toArray(String[]::new);
        this.fblAllowedHeaders = fblAllowedHeaders;

        this.allowedMethods = allowedMethods;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/explore/**")
                .allowedOrigins(exploreAllowedHosts)
                .allowedHeaders(exploreAllowedHeaders)
                .allowedMethods(allowedMethods);

        registry
                .addMapping("/formfbl/**")
                .allowedOrigins(fblAllowedHosts)
                .allowedHeaders(fblAllowedHeaders)
                .allowedMethods(allowedMethods)
                .allowCredentials(true);
    }
}
