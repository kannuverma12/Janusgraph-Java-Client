package com.paytm.digital.education.explore.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class AwsConfig {

    private static String clientRegion;
    private static String serviceEndPoint;

    @Value("${aws.s3.region}")
    public void setClientRegion(String region) {
        clientRegion = region;
    }

    @Value("${aws.s3.endpoint}")
    public void setServiceEndPoint(String endPoint) {
        serviceEndPoint = endPoint;
    }

    public static AmazonS3 func() {
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new ProfileCredentialsProvider())
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(serviceEndPoint,
                                    clientRegion))
                    .build();
            return s3Client;
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            log.error("Error In AWS.func() with AmazonServiceException ", e);
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            log.error("Error In AWS.func() with SdkClientException ", e);
        }
        return null;
    }
}
