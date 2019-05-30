package com.paytm.digital.education.form.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

/**
 * AWS S3 File Upload Class.
 */
@Slf4j
@Service
@AllArgsConstructor
@NoArgsConstructor
public class S3FileService {

    @Value("${s3.bucketName}")
    private String bucketName;

    private AmazonS3 s3Client;

    public void initializeS3Client() {
        try {
            this.s3Client = AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(new InstanceProfileCredentialsProvider(false))
                    .build();
        } catch (AmazonClientException ace) {
            log.error(" Caught an AmazonClientException, which "
                    + "means an internal error occured while " + "initializing the client."
                    + "ERROR MESSAGE: {}", ace.getMessage());
        } catch (Exception e) {
            log.error("ERROR MESSAGE: {}", e.getMessage());
        }
    }

    public String get(String s3FileName) {
        return s3Client.getObjectAsString(bucketName, s3FileName);
    }

    public void put(String localFilePath, String s3FileName) {
        put(localFilePath, s3FileName, false);
    }

    public void put(String localFilePath, String s3FileName, boolean overwrite) {
        File localFile = new File(localFilePath);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName, localFile);
        putObject(putObjectRequest, s3FileName, overwrite);
    }

    public void put(InputStream sourceStream, String s3FileName) {
        put(sourceStream, s3FileName, false);
    }

    public void put(InputStream sourceStream, String s3FileName, boolean overwrite) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName, sourceStream, null);
        putObject(putObjectRequest, s3FileName, overwrite);
    }

    private void putObject(PutObjectRequest putObjectRequest, String keyName) {
        putObject(putObjectRequest, keyName, false);
    }

    private void putObject(PutObjectRequest putObjectRequest, String keyName, boolean overwrite) {
        try {
            if (overwrite || !s3Client.doesObjectExist(bucketName, keyName)) {
                s3Client.putObject(putObjectRequest);
            }
        } catch (AmazonServiceException ase) {
            log.info("Caught an AmazonServiceException, which " + "means your request made it "
                    + "to Amazon S3, but was rejected with an error response" + " for some reason."
                    + "ERROR MESSAGE: {}", ase.getMessage());
            log.error("HTTP Status Code: " + ase.getStatusCode());
            log.error("AWS Error Code:   " + ase.getErrorCode());
            log.error("Error Type:       " + ase.getErrorType());
            log.error("Request ID:       " + ase.getRequestId());
            throw ase;
        } catch (AmazonClientException ace) {
            log.error(
                    "Caught an AmazonClientException, which " + "means the client encountered "
                            + "an internal error while trying to " + "communicate with S3, "
                            + "such as not being able to access the network." + "ERROR MESSAGE: {}",
                    ace.getMessage());
            throw ace;
        } catch (Exception e) {
            log.error("Caught some unknown Exception" + "ERROR MESSAGE: {}", e.getMessage());
            throw e;
        }
    }

}
