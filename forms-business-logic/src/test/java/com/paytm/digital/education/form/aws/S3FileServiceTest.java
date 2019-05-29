package com.paytm.digital.education.form.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class S3FileServiceTest {

    private static S3FileService s3FileService;

    @BeforeAll
    public static void init() {
        S3Mock api = new S3Mock.Builder().withPort(8001).withInMemoryBackend().build();
        api.start();

        /* AWS S3 client setup.
         *  withPathStyleAccessEnabled(true) trick is required to overcome S3 default
         *  DNS-based bucket access scheme
         *  resulting in attempts to connect to addresses like "bucketname.localhost"
         *  which requires specific DNS setup.
         */
        AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(
                "http://localhost:8001", "us-west-2");

        AmazonS3 amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpoint)
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .build();

        String bucketName = "testbucket";
        amazonS3.createBucket(bucketName);
        s3FileService = new S3FileService(bucketName, amazonS3);
    }

    @Test
    public void s3PutGetTest() {
        String sampleText = "Hello S3 World !!";
        String s3FileName = "/education/form/star_war.txt";
        InputStream targetStream = new ByteArrayInputStream(sampleText.getBytes());
        s3FileService.put(targetStream, s3FileName, false);
        assertEquals(sampleText, s3FileService.get(s3FileName));
    }

}
