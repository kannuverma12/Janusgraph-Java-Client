package com.paytm.digital.education.utility;

import com.paytm.digital.education.service.S3Service;
import com.paytm.digital.education.service.SftpService;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import javafx.util.Pair;
import lombok.AllArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Map;

import static com.paytm.digital.education.constant.GoogleUtilConstant.FILENAME;
import static com.paytm.digital.education.constant.GoogleUtilConstant.GOOGLE_DRIVE_BASE_URL;
import static com.paytm.digital.education.constant.GoogleUtilConstant.INPUTSTREAM;
import static com.paytm.digital.education.constant.GoogleUtilConstant.MIMETYPE;


@Service
@AllArgsConstructor
public class UploadUtil {
    /*
     ** Upload to S3
     */
    private static final Logger log = LoggerFactory.getLogger(UploadUtil.class);

    private S3Service s3Service;

    public Pair<String, String> uploadFile(String fileUrl, String fileName, Long entityId,
            String s3ImagePath, String s3BucketName, String clientSecretFileName,
            String clientSecretFolder) {
        InputStream inputStream = null;
        String mimeType = null;
        fileUrl = fileUrl.trim();
        try {
            if (fileUrl.startsWith(GOOGLE_DRIVE_BASE_URL)) {
                Map<String, Object> fileData =
                        GoogleDriveUtil
                                .downloadFile(fileUrl, clientSecretFileName,
                                        clientSecretFolder);
                inputStream = (InputStream) fileData.get(INPUTSTREAM);
                fileName =
                        (fileName == null ? "" : fileName + "_") + (String) fileData.get(FILENAME);
                mimeType = (String) fileData.get(MIMETYPE);
            }
            String relativePath = MessageFormat.format(s3ImagePath, entityId);
            log.info("relativePath: {}", relativePath);
            String imageUrl =
                    s3Service.uploadFile(inputStream, fileName, entityId,
                            relativePath, s3BucketName);
            log.info("centerImage: {}", imageUrl);
            return new Pair<>(imageUrl, mimeType);
        } catch (Exception e) {
            log.error("Unable to upload file for file : {} and the error:",
                    fileUrl, e);
        }
        return new Pair<>(null, null);
    }

    public String uploadImage(String fileUrl, String fileName, Long instituteId,
            String s3BucketName, String s3ImagePath) {
        System.setProperty("http.agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) "
                        + "Chrome/28.0.1500.29 Safari/537.36");
        try {
            URL url = new URL(fileUrl);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) "
                            + "Chrome/28.0.1500.29 Safari/537.36");
            InputStream stream = connection.getInputStream();
            //InputStream stream = url.openStream();
            String relativePath = MessageFormat.format(s3ImagePath, instituteId.toString());
            log.info("Relative_path : {}", relativePath);
            String s3RelativeUrl = s3Service
                    .uploadFile(stream, fileName, instituteId,
                            relativePath, s3BucketName);
            log.info("S3 relative url: {}", s3RelativeUrl);
            if (StringUtils.isNotBlank(s3RelativeUrl)) {
                return s3RelativeUrl;
            }
        } catch (MalformedURLException e) {
            log.error("Url building malformed for url string :{} and error is {} ",
                    fileUrl, JsonUtils.toJson(e.getMessage()));
        } catch (IOException e) {
            log.error("IO Exception while downloading file for url :{} and the error is {}",
                    fileUrl, JsonUtils.toJson(e.getMessage()));
        }
        return null;
    }
}
