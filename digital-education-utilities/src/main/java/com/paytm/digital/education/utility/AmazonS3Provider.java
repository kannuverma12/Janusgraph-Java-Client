package com.paytm.digital.education.utility;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.paytm.digital.education.config.AwsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Slf4j
@Service
@EnableScheduling
public class AmazonS3Provider {

    private final Environment environment;

    private AmazonS3 amazonS3;

    @Autowired
    public AmazonS3Provider(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    @Scheduled(fixedDelayString = "${aws.bean.reinitialise.delay}")
    private void initialize() {
        if (Arrays.stream(environment.getActiveProfiles()).anyMatch(
            env -> (env.equalsIgnoreCase("dev")
                || env.equalsIgnoreCase("default") || env.equalsIgnoreCase("local")))) {
            log.info("initialized s3 client for openstack server");
            amazonS3 = getLocalS3Client();
        } else {
            log.info("created s3 client using IAMRole info from ec2 {}", AwsConfig.getClientRegion());
            amazonS3 = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_1)
                    .enableForceGlobalBucketAccess()
                    .build();
        }
    }

    public AmazonS3 getAmazonS3() {
        return amazonS3;
    }

    private AmazonS3 getLocalS3Client() {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
        builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http"
                + "://localhost:4567", AwsConfig.getClientRegion()));
        builder.enablePathStyleAccess();
        return builder.build();
    }
}

