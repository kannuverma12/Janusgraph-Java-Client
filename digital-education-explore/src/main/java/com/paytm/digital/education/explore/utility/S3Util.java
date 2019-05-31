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
import java.text.MessageFormat;
import java.util.Objects;

import static com.paytm.digital.education.explore.constants.AWSConstants.CHROME_HTTP_AGENT;
import static com.paytm.digital.education.explore.constants.AWSConstants.HTTP_AGENT;

@Slf4j
@UtilityClass
public class S3Util {
    private final AmazonS3 s3Client = AwsConfig.func();

    public String uploadFile(String mediaUrl, InputStream inputStream, String fileName,
            String bucketPath,
            Long instituteId, String s3MediaFolderRelativePath)
            throws IOException {
        log.info("In S3Service.uploadFile with imageUrl {} fileName {} instituteId {} ", mediaUrl,
                fileName, instituteId);
        System.setProperty(HTTP_AGENT, CHROME_HTTP_AGENT);
        if (Objects.nonNull(mediaUrl)) {
            mediaUrl = encodeString(mediaUrl, fileName);
            URL url = new URL(mediaUrl);
            inputStream = url.openStream();
        }
        String s3Path = MessageFormat.format(s3MediaFolderRelativePath, instituteId);
        System.out.println(s3MediaFolderRelativePath);
        System.out.println(s3Path);
        System.out.println(fileName);
        ObjectMetadata metadata = new ObjectMetadata();
        byte[] bytes1 = IOUtils.toByteArray(inputStream);
        metadata.setContentLength(bytes1.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes1);
        PutObjectRequest saveRequest =
                new PutObjectRequest(
                        bucketPath + s3Path,
                        fileName, byteArrayInputStream, metadata);
        s3Client.putObject(saveRequest);
        inputStream.close();
        log.info(
                "Exited from S3Service.uploadFile with imageUrl {} fileName {} instituteId {} s3Path {} ",
                mediaUrl, fileName, instituteId, s3Path);
        return s3Path + fileName;
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
