package com.paytm.digital.education.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

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
    private static String coachingCourseLogoPrefix;
    private static String coachingInstituteLogoPrefix;

    private static String envProfile;
    private static String coachingS3BucketName;
    private static String coachingS3Path;
    private static String coachingStreamLogoPrefix;
    private static String coachingInstituteImagePrefix;
    private static String coachingBannerImagePrefix;
    private static String coachingCourseFeatureLogoPrefix;
    private static String coachingTopRankerImagePrefix;

    public static String getEnvProfile() {
        return envProfile;
    }

    @Value("${ingestion.env.profile}")
    public static void setEnvProfile(String envProfile) {
        ConfigProperties.envProfile = envProfile;
    }

    @Value("${coaching.s3.path}")
    public static void setCoachingS3Path(String coachingS3Path) {
        ConfigProperties.coachingS3Path = coachingS3Path;
    }

    @Value("${coaching.s3.bucketName}")
    public static void setCoachingS3BucketName(String coachingS3BucketName) {
        ConfigProperties.coachingS3BucketName = coachingS3BucketName;
    }

    @Value("${coaching.stream.logo.prefix}")
    public static void setCoachingStreamLogoPrefix(String coachingStreamLogoPrefix) {
        ConfigProperties.coachingStreamLogoPrefix = coachingStreamLogoPrefix;
    }

    @Value("${coaching.institute.image.prefix}")
    public static void setCoachingInstituteImagePrefix(String coachingInstituteImagePrefix) {
        ConfigProperties.coachingInstituteImagePrefix = coachingInstituteImagePrefix;
    }

    @Value("${coaching.banner.image.prefix}")
    public static void setCoachingBannerImagePrefix(String coachingBannerImagePrefix) {
        ConfigProperties.coachingBannerImagePrefix = coachingBannerImagePrefix;
    }

    @Value("${coaching.course.feature.logo.prefix}")
    public static void setCoachingCourseFeatureLogoPrefix(String coachingCourseFeatureLogoPrefix) {
        ConfigProperties.coachingCourseFeatureLogoPrefix = coachingCourseFeatureLogoPrefix;
    }

    @Value("${coaching.topranker.image.prefix}")
    public static void setCoachingTopRankerImagePrefix(String coachingTopRankerImagePrefix) {
        ConfigProperties.coachingTopRankerImagePrefix = coachingTopRankerImagePrefix;
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

    @Value("${coaching.course.logo.prefix}")
    public void setCoachingCourseLogoPrefix(String logoPrefix) {
        coachingCourseLogoPrefix = logoPrefix;
    }

    @Value("${coaching.institute.logo.prefix}")
    public void setCoachingInstituteLogoPrefix(String logoPrefix) {
        coachingInstituteLogoPrefix = logoPrefix;
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

    public static String getCoachingCourseLogoPrefix() {
        return coachingCourseLogoPrefix;
    }

    public static String getCoachingInstituteLogoPrefix() {
        return coachingInstituteLogoPrefix;
    }

    public static String getCoachingS3BucketName() {
        return coachingS3BucketName;
    }

    public static String getCoachingS3Path() {
        return coachingS3Path;
    }

    public static String getCoachingStreamLogoPrefix() {
        return coachingStreamLogoPrefix;
    }

    public static String getCoachingInstituteImagePrefix() {
        return coachingInstituteImagePrefix;
    }

    public static String getCoachingBannerImagePrefix() {
        return coachingBannerImagePrefix;
    }

    public static String getCoachingCourseFeatureLogoPrefix() {
        return coachingCourseFeatureLogoPrefix;
    }

    public static String getCoachingTopRankerImagePrefix() {
        return coachingTopRankerImagePrefix;
    }
}
