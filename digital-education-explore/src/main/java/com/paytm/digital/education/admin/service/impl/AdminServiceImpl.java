package com.paytm.digital.education.admin.service.impl;

import static com.paytm.digital.education.explore.constants.ExploreConstants.DIRECTORY_SEPARATOR_SLASH;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_EXPLORE_PREFIX;

import com.paytm.digital.education.admin.response.DocumentUploadResponse;
import com.paytm.digital.education.admin.service.AdminService;
import com.paytm.digital.education.config.AwsConfig;
import com.paytm.digital.education.service.S3Service;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);

    private S3Service s3Service;

    @Override
    public List<DocumentUploadResponse> uploadDocument(List<MultipartFile> files, String type,
            String relativeUrl) {
        List<DocumentUploadResponse> responseList = new ArrayList<>();
        for (MultipartFile file : files) {
            responseList.add(uploadDocument(file, type, relativeUrl));
        }
        return responseList;
    }

    private DocumentUploadResponse uploadDocument(MultipartFile file, String type,
            String relativeUrl) {
        String fileName = file.getOriginalFilename().trim();
        relativeUrl = trimRelativeUrl(relativeUrl);
        String absoluteUrl = AwsConfig.getMediaBaseUrl() + EDUCATION_EXPLORE_PREFIX + type;
        if (StringUtils.isNotBlank(relativeUrl)) {
            absoluteUrl = absoluteUrl + DIRECTORY_SEPARATOR_SLASH + relativeUrl;
        }
        DocumentUploadResponse response = new DocumentUploadResponse();
        InputStream fileInputStream = null;
        try {
            response.setFileName(fileName);
            response.setFileType(file.getContentType());
            String educationBucket = AwsConfig.getEducationExploreBucketName();
            fileInputStream = file.getInputStream();
            s3Service.uploadFile(
                    fileInputStream, fileName, fileName,
                    type + DIRECTORY_SEPARATOR_SLASH + relativeUrl,
                    educationBucket);
            response.setFileUrl(absoluteUrl + DIRECTORY_SEPARATOR_SLASH + fileName);
        } catch (Exception ex) {
            log.error("Error caught while uploading documents : {}", ex);
            response.setError(ex.getMessage());
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException ex) {
                log.error("Error in closing file stream for admin service in document upload : ", ex);
            }
        }
        return response;
    }

    private String trimRelativeUrl(String relativeUrl) {
        if (StringUtils.isBlank(relativeUrl)) {
            return relativeUrl;
        }
        if (relativeUrl.startsWith(DIRECTORY_SEPARATOR_SLASH)) {
            relativeUrl = relativeUrl.substring(1);
        }
        if (relativeUrl.endsWith(DIRECTORY_SEPARATOR_SLASH)) {
            relativeUrl = relativeUrl.substring(0, relativeUrl.length() - 1);
        }
        return relativeUrl;
    }
}
