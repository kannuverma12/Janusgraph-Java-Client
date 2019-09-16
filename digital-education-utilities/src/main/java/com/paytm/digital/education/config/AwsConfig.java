package com.paytm.digital.education.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AwsConfig {

    private static String clientRegion;
    private static String s3ExploreBucketName;
    private static String educationExploreBucketName;
    private static String s3CoachingBucketName;
    private static String relativePathPrefix;
    private static String mediaBaseUrl;
    private static String s3ExploreBucketNameWithoutSuffix;

    @Value("${aws.s3.region}")
    public void setClientRegion(String region) {
        clientRegion = region;
    }

    @Value("${aws.s3.education.explore.bucketname}")
    public void setEducationExploreBucketName(String bucketName) {
        educationExploreBucketName = bucketName;
    }

    @Value("${aws.s3.explore.bucketname}")
    public void setS3ExploreBucketName(String bucketName) {
        s3ExploreBucketName = bucketName;
    }

    @Value("${aws.s3.coaching.bucketname}")
    public void setS3CoachingBucketName(String bucketName) {
        s3CoachingBucketName = bucketName;
    }

    @Value("${aws.s3.relativepath.prefix}")
    public void setRelativePathPrefix(String prefix) {
        relativePathPrefix = prefix;
    }

    @Value("${education.asset.baseurl}")
    public void setBaseUrl(String baseUrl) {
        mediaBaseUrl = baseUrl;
    }

    @Value("${aws.s3.explore.bucketname.without.suffix}")
    public void setS3ExploreBucketNameWithoutSuffix(String bucketName) {
        s3ExploreBucketNameWithoutSuffix = bucketName;
    }

    public static String getMediaBaseUrl() {
        return mediaBaseUrl;
    }

    public static String getRelativePathPrefix() {
        return relativePathPrefix;
    }

    public static String getEducationExploreBucketName() {
        return educationExploreBucketName;
    }

    public static String getS3ExploreBucketName() {
        return s3ExploreBucketName;
    }

    public static String getS3ExploreBucketNameWithoutSuffix() {
        return s3ExploreBucketNameWithoutSuffix;
    }

    public static String getS3CoachingBucketName() {
        return s3CoachingBucketName;
    }

    public static String getClientRegion() {
        return clientRegion;
    }

}
