package com.paytm.digital.education.utility;

import com.paytm.digital.education.service.S3Service;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Map;

import static com.paytm.digital.education.constant.GoogleUtilConstant.FILENAME;
import static com.paytm.digital.education.constant.GoogleUtilConstant.GOOGLE_DRIVE_BASE_URL;
import static com.paytm.digital.education.constant.GoogleUtilConstant.INPUTSTREAM;
import static com.paytm.digital.education.constant.GoogleUtilConstant.MIMETYPE;

@Slf4j
@Service
@AllArgsConstructor
public class UploadUtil {
    /*
     ** Upload to S3
     */
    private S3Service s3Service;

    public Pair<String, String> uploadFile(String fileUrl, String fileName, Long instituteId,
            String s3ImagePath, String s3BucketName, String clientSecretFileName) {
        InputStream inputStream = null;
        String mimeType = null;
        fileUrl = fileUrl.trim();
        try {
            if (fileUrl.startsWith(GOOGLE_DRIVE_BASE_URL)) {
                Map<String, Object> fileData =
                        GoogleDriveUtil.downloadFile(fileUrl, clientSecretFileName);
                inputStream = (InputStream) fileData.get(INPUTSTREAM);
                fileName = (String) fileData.get(FILENAME);
                mimeType = (String) fileData.get(MIMETYPE);
            }
            String relativePath = MessageFormat.format(s3ImagePath, instituteId);
            String imageUrl =
                    s3Service.uploadFile(inputStream, fileName, instituteId,
                            relativePath, s3BucketName);
            return new Pair<>(imageUrl, mimeType);
        } catch (Exception e) {
            log.error("Unable to upload file for file : {} and the error is {}",
                    fileUrl, JsonUtils.toJson(e.getMessage()));
        }
        return new Pair<>(null, null);
    }
}
