package com.paytm.digital.education.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.paytm.digital.education.utility.AmazonS3Provider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class S3Service {

    private final AmazonS3Provider s3Provider;

    @Autowired
    public S3Service(AmazonS3Provider s3Provider) {
        this.s3Provider = s3Provider;
    }

    public InputStream downloadFile(String key, String bucketName) {
        S3Object object = s3Provider.getAmazonS3().getObject(bucketName, key);
        return object.getObjectContent();
    }

    public String uploadFile(InputStream inputStream, String fileName,
            Object entityId, String relativePath, String s3BucketName) throws IOException {

        ObjectMetadata metadata = new ObjectMetadata();
        byte[] bytes1 = IOUtils.toByteArray(inputStream);
        metadata.setContentLength(bytes1.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes1);
        PutObjectRequest objectRequest = new PutObjectRequest(s3BucketName + relativePath,
                fileName,
                byteArrayInputStream, metadata);
        objectRequest.withCannedAcl(CannedAccessControlList.PublicReadWrite);
        PutObjectResult result = s3Provider.getAmazonS3().putObject(objectRequest);
        inputStream.close();
        log.info("Exited from S3Service.uploadFile with fileName {} EntityId {} s3Path {} ",
                fileName, entityId, relativePath);
        return relativePath + "/" + fileName;
    }
}

