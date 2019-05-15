package com.paytm.digital.education.explore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class ConfigProperties {

    private static String mediaBaseUrl;
    private static String logoImagePrefix;
    private static String facilitiesIconPrefix;
    private static String highlightsIconPrefix;
    
    @Value("${education.asset.baseurl}")
    public void setBaseUrl(String baseUrl) {
        mediaBaseUrl = baseUrl;
    }
    
    @Value("${institute.gallery.image.prefix}")
    public void setLogoImagePrefix(String logoPrefix) {
        logoImagePrefix = logoPrefix;
    }

    @Value("${institute.facilities.icon.prefix}")
    public void setFacilitiesIconPrefix(String iconPrefix) {
        facilitiesIconPrefix = iconPrefix;
    }

    @Value("${highlights.icon.prefix}")
    public void setHighlightsIconPrefix(String iconPrefix) {
        highlightsIconPrefix = iconPrefix;
    }
    
    public static String getBaseUrl() {
        return mediaBaseUrl;
    }
    
    public static String getLogoImagePrefix() {
        return logoImagePrefix;
    }

    public static String getFacilitiesIconPrefix() {
        return facilitiesIconPrefix;
    }

    public static String getHighlightsIconPrefix() {
        return highlightsIconPrefix;
    }

}
