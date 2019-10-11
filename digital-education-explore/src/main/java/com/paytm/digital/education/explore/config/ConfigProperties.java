package com.paytm.digital.education.explore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class ConfigProperties {

    private static String logoExamPrefix;
    private static String mediaBaseUrl;
    private static String logoImagePrefix;
    private static String facilitiesIconPrefix;
    private static String highlightsIconPrefix;
    private static String highlightsIconPrefixApp;
    private static String streamIconPrefix;
    private static String locationIconPrefix;
    private static String bannerPrefix;
    private static String appFooterPrefix;
    private static String rankingLogoPrefix;
    private static String examLogoPrefix;
    private static String ctaLogoPrefix;
    private static String schoolLogoPrefix;
    private static String examSectionIconPrefix;
    private static String examPlaceholderLogoURL;

    @Value("${exam.detail.icon.prefix}")
    public void setExamSectionIconPrefix(String prefix) {
        examSectionIconPrefix = prefix;
    }

    @Value("${cta.logo.prefix}")
    public void setCtaLogoPrefix(String prefix) {
        ctaLogoPrefix = prefix;
    }

    @Value("${education.asset.baseurl}")
    public void setBaseUrl(String baseUrl) {
        mediaBaseUrl = baseUrl;
    }

    @Value("${app.footer.prefix}")
    public void setAppFooterPrefix(String logoPrefix) {
        appFooterPrefix = logoPrefix;
    }

    @Value("${institute.gallery.image.prefix}")
    public void setLogoImagePrefix(String logoPrefix) {
        logoImagePrefix = logoPrefix;
    }

    @Value("${exam.logo.prefix}")
    public void setLogoExamPrefix(String logoPrefix) {
        logoExamPrefix = logoPrefix;
    }

    @Value("${institute.facilities.icon.prefix}")
    public void setFacilitiesIconPrefix(String iconPrefix) {
        facilitiesIconPrefix = iconPrefix;
    }

    @Value("${highlights.icon.app.prefix}")
    public void setHighlightsIconPrefixApp(String iconPrefix) {
        highlightsIconPrefixApp = iconPrefix;
    }

    @Value("${highlights.icon.prefix}")
    public void setHighlightsIconPrefix(String iconPrefix) {
        highlightsIconPrefix = iconPrefix;
    }

    @Value("${streams.icon.prefix}")
    public void setStreamIconPrefix(String iconPrefix) {
        streamIconPrefix = iconPrefix;
    }

    @Value("${banner.prefix}")
    public void setBannerPrefix(String iconPrefix) {
        bannerPrefix = iconPrefix;
    }

    @Value("${locations.icon.prefix}")
    public void setLocationIconPrefix(String iconPrefix) {
        locationIconPrefix = iconPrefix;
    }

    @Value("${ranking.logo.prefix}")
    public void setRankingLogoPrefix(String logoPrefix) {
        rankingLogoPrefix = logoPrefix;
    }

    @Value("${educaton.exam.logo.prefix}")
    public void setExamLogoPrefix(String logoPrefix) {
        examLogoPrefix = logoPrefix;
    }

    @Value("${education.school.logo.prefix}")
    public void setSchoolLogoPrefix(String logoPrefix) {
        schoolLogoPrefix = logoPrefix;
    }

    @Value("${exam.placeholder.logo.url}")
    public void setExamPlaceHolderLogo(String logo) {
        examPlaceholderLogoURL = logo;
    }

    public static String getBaseUrl() {
        return mediaBaseUrl;
    }

    public static String getLogoExamPrefix() {
        return logoExamPrefix;
    }

    public static String getAppFooterPrefix() {
        return appFooterPrefix;
    }

    public static String getRankingLogo() {
        return rankingLogoPrefix;
    }

    public static String getExamLogoPrefix() {
        return examLogoPrefix;
    }

    public static String getSchoolLogoPrefix() {
        return schoolLogoPrefix;
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

    public static String getHighlightsIconPrefixApp() {
        return highlightsIconPrefixApp;
    }

    public static String getStreamIconPrefix() {
        return streamIconPrefix;
    }

    public static String getLocationIconPrefix() {
        return locationIconPrefix;
    }

    public static String getBannerPrefix() {
        return bannerPrefix;
    }

    public static String getCtaLogoPrefix() {
        return ctaLogoPrefix;
    }

    public static String getExamPlaceholderLogoURL() {
        return examPlaceholderLogoURL;
    }

    public static String getExamSectionIconPrefix() {
        return examSectionIconPrefix;
    }
}
