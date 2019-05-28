package com.paytm.digital.education.explore.utility;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.paytm.digital.education.explore.config.AwsConfig;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.Objects;

import static com.paytm.digital.education.explore.constants.AWSConstants.CHROME_HTTP_AGENT;
import static com.paytm.digital.education.explore.constants.AWSConstants.HTTP_AGENT;
import static com.paytm.digital.education.explore.constants.AWSConstants.S3_BUCKET_PATH;
import static com.paytm.digital.education.explore.constants.AWSConstants.S3_PATH;

@Slf4j
@UtilityClass
public class S3Util {
    private final AmazonS3 s3Client = AwsConfig.func();

    public String uploadFile(String imageUrl, InputStream inputStream, String fileName,
            String bucketPath,
            Long instituteId, String s3ImagePath)
            throws IOException, GeneralSecurityException {
        log.info("In S3Service.uploadFile with imageUrl {} fileName {} instituteId {} ", imageUrl,
                fileName, instituteId);
        System.setProperty(HTTP_AGENT, CHROME_HTTP_AGENT);
//        if (Objects.nonNull(imageUrl)) {
//            imageUrl = encodeString(imageUrl, fileName);
//            URL url = new URL(imageUrl);
//            inputStream = url.openStream();
//        }
//        ObjectMetadata metadata = new ObjectMetadata();
//        byte[] bytes1 = IOUtils.toByteArray(inputStream);
//        metadata.setContentLength(bytes1.length);
//        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes1);
//        PutObjectRequest saveRequest =
//                new PutObjectRequest(
//                        bucketPath,
//                        fileName, byteArrayInputStream, metadata);
//        s3Client.putObject(saveRequest);
//        inputStream.close();
        String s3Path = MessageFormat.format(s3ImagePath, instituteId, fileName);
        log.info(
                "Exited from S3Service.uploadFile with imageUrl {} fileName {} instituteId {} s3Path {} ",
                imageUrl, fileName, instituteId, s3Path);
        return s3Path;
    }

    public String encodeString(String url, String fileName) {
        if (StringUtils.isNotBlank(url) && StringUtils.contains(url, "/")) {
            String baseUrl = url.substring(0, url.lastIndexOf("/") + 1);
            try {
                String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
                url = baseUrl + encodedFileName;
            } catch (UnsupportedEncodingException e) {
                log.error("Error in encodeString for url " + url, e);
            }
        }
        return url;
    }
}
