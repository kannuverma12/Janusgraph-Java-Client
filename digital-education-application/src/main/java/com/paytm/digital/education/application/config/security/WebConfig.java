package com.paytm.digital.education.application.config.security;

import com.paytm.digital.education.explore.controller.CTAEntityConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
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
    private final String[] coachingAllowedHosts;
    private final String[] coachingAllowedHeaders;


    public WebConfig(
            @Value("#{'${cors.allowed.hosts}'.split(',')}") final String[] exploreAllowedHosts,
            @Value("#{'${cors.allowed.headers}'.split(',')}") final String[] exploreAllowedHeaders,

            @Value("#{'${fbl.cors.allowed.hosts}'.split(',')}") final String[] fblAllowedHosts,
            @Value("#{'${fbl.cors.allowed.headers}'.split(',')}") final String[] fblAllowedHeaders,

            @Value("#{'${coaching.cors.allowed.hosts}'.split(',')}") final String[] coachingAllowedHosts,
            @Value("#{'${coaching.cors.allowed.headers}'.split(',')}") final String[] coachingAllowedHeaders,

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

        this.coachingAllowedHosts = Arrays.stream(coachingAllowedHosts)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotBlank)
                .flatMap(s -> Stream.of("http://" + s, "https://" + s))
                .toArray(String[]::new);
        this.coachingAllowedHeaders = coachingAllowedHeaders;

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

        registry
                .addMapping("/coaching/**")
                .allowedOrigins(coachingAllowedHosts)
                .allowedHeaders(coachingAllowedHeaders)
                .allowedMethods(allowedMethods);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new CTAEntityConverter());
    }
}
