package com.paytm.digital.education.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AwsConfig {

    private static String clientRegion;
    private static String s3ExploreBucketName;
    private static String s3CoachingBucketName;
    private static String relativePathPrefix;

    @Value("${aws.s3.region}")
    public void setClientRegion(String region) {
        clientRegion = region;
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

    public static String getRelativePathPrefix() {
        return relativePathPrefix;
    }

    public static String getS3ExploreBucketName() {
        return s3ExploreBucketName;
    }

    public static String getS3CoachingBucketName() {
        return s3CoachingBucketName;
    }

    public static String getClientRegion() {
        return clientRegion;
    }

}
