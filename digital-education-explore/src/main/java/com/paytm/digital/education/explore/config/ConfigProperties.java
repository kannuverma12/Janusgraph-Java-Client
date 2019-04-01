package com.paytm.digital.education.explore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class ConfigProperties {

    private static String mediaBaseUrl;
    private static String logoImagePrefix;
    
    @Value("${education.asset.baseurl}")
    public void setBaseUrl(String baseUrl) {
        mediaBaseUrl = baseUrl;
    }
    
    @Value("${institute.gallery.image.prefix}")
    public void setLogoImagePrefix(String logoPrefix) {
        logoImagePrefix = logoPrefix;
    }
    
    public static String getBaseUrl() {
        return mediaBaseUrl;
    }
    
    public static String getLogoImagePrefix() {
        return logoImagePrefix;
    }

}
