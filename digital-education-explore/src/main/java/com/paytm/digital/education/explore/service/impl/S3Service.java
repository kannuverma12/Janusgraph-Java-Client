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
import com.paytm.digital.education.explore.constants.ExploreConstants;
import com.paytm.digital.education.explore.es.model.Aws;
import com.paytm.digital.education.explore.utility.CommonUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
public class S3Service {

    private final AmazonS3 s3Client = Aws.func();

    public String uploadFile(String imageUrl, String fileName, Long instituteId)
            throws IOException {
        log.info("In S3Service.uploadFile with imageUrl {} fileName {} instituteId {} ", imageUrl,
                fileName, instituteId);

        imageUrl = CommonUtil.encodeString(imageUrl, fileName);
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        ObjectMetadata metadata = new ObjectMetadata();
        byte[] bytes1 = IOUtils.toByteArray(is);
        metadata.setContentLength(bytes1.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes1);
        PutObjectRequest saveRequest =
                new PutObjectRequest(
                        ExploreConstants.S3_BUCKET_PATH + instituteId,
                        fileName, byteArrayInputStream, metadata);
        s3Client.putObject(saveRequest);
        is.close();
        String s3Path = "/" + instituteId + "/" + fileName;
        log.info(
                "Exited from S3Service.uploadFile with imageUrl {} fileName {} instituteId {} s3Path {} ",
                imageUrl, fileName, instituteId, s3Path);
        return s3Path;
    }

}
