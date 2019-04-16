package com.paytm.digital.education.explore.service.impl;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.paytm.digital.education.explore.es.model.Aws;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
public class S3Service {

    private final AmazonS3 s3Client = Aws.func();

    public String uploadFile(String imageUrl, String fileName, Long instituteId) throws IOException {
        log.info("In S3Service.uploadFile with imageUrl {} fileName {} instituteId {} ", imageUrl, fileName, instituteId);
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        ObjectMetadata metadata = new ObjectMetadata();
        byte[] bytes = IOUtils.toByteArray(is);
        metadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        PutObjectRequest saveRequest =
                new PutObjectRequest(
                        "assets.paytm.com/educationwebassets/education/explore/college/images/"
                                + instituteId + "/",
                        fileName, byteArrayInputStream, metadata);
        s3Client.putObject(saveRequest);
        String s3Path =
                "http://assetscdn1.paytm.com/educationwebassets/education/explore/college/images/"
                        + instituteId + "/" + fileName;
        is.close();
        log.info("Exited from S3Service.uploadFile with imageUrl {} fileName {} instituteId {} s3Path {} ", imageUrl, fileName, instituteId, s3Path);
        return s3Path;
    }

}
