package com.paytm.digital.education.explore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class AwsConfig {

    private static String clientRegion;
    private static String serviceEndPoint;
    private static String s3BucketPath;
    private static String relativePathPrefix;
    private static String s3IamRole;
    private static String s3BucketName;
    private static String s3RegionName;

    @Value("${aws.s3.region}")
    public void setClientRegion(String region) {
        clientRegion = region;
    }

    @Value("${aws.s3.bucketpath}")
    public void setS3BucketPath(String bucketPath) {
        s3BucketPath = bucketPath;
    }

    @Value("${aws.s3.relativepath.prefix}")
    public void setRelativePathPrefix(String prefix) {
        relativePathPrefix = prefix;
    }

    @Value("${s3.bucket.name}")
    public void setS3BucketName(String bucketName) {
        s3BucketName = bucketName;
    }

    public static String getRelativePathPrefix() {
        return relativePathPrefix;
    }

    public static String getS3BucketPath() {
        return s3BucketPath;
    }

    public static String getClientRegion() {
        return clientRegion;
    }

}
