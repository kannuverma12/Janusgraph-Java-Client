package com.paytm.digital.education.explore.service;

import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.paytm.digital.education.explore.config.AwsConfig;
import com.paytm.digital.education.explore.utility.AmazonS3Provider;
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

    public InputStream downloadFile(String key) {
        S3Object object = s3Provider.getAmazonS3().getObject(AwsConfig.getS3BucketPath(), key);
        return object.getObjectContent();
    }

    public String uploadFile(InputStream inputStream, String fileName,
            Long instituteId, String relativePath) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        byte[] bytes1 = IOUtils.toByteArray(inputStream);
        metadata.setContentLength(bytes1.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes1);
        PutObjectRequest objectRequest = new PutObjectRequest(AwsConfig.getS3BucketPath() + relativePath, fileName,
                byteArrayInputStream, metadata);
        objectRequest.withCannedAcl(CannedAccessControlList.PublicReadWrite);
        PutObjectResult result = s3Provider.getAmazonS3().putObject(objectRequest);
        inputStream.close();
        log.info(
                "Exited from S3Service.uploadFile with fileName {} instituteId {} s3Path {} ",
                fileName, instituteId, relativePath);
        return relativePath + "/" + fileName;
    }
}
