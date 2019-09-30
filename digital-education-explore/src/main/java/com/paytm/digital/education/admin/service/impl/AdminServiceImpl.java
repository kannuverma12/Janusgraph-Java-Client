package com.paytm.digital.education.admin.service.impl;

import com.paytm.digital.education.admin.response.DocumentUploadResponse;
import com.paytm.digital.education.admin.service.AdminService;
import com.paytm.digital.education.config.AwsConfig;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.entity.School;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.service.S3Service;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.paytm.digital.education.constant.CommonConstants.LOGO;
import static com.paytm.digital.education.constant.ExploreConstants.COLLEGE;
import static com.paytm.digital.education.constant.ExploreConstants.DIRECTORY_SEPARATOR_SLASH;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.constant.ExploreConstants.GALLERY_LOGO;
import static com.paytm.digital.education.constant.ExploreConstants.HIGHLIGHT;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_ID;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);

    private S3Service             s3Service;
    private CommonMongoRepository commonMongoRepository;

    @Override
    public List<DocumentUploadResponse> uploadDocument(List<MultipartFile> files, String type,
            String relativeUrl, Long entityId) {
        List<DocumentUploadResponse> responseList = new ArrayList<>();
        for (MultipartFile file : files) {
            responseList.add(uploadDocument(file, type, relativeUrl, entityId));
        }
        return responseList;
    }

    private DocumentUploadResponse uploadDocument(MultipartFile file, String type,
            String relativeUrl, Long entityId) {

        DocumentUploadResponse response = new DocumentUploadResponse();
        Object obj = null;
        if (!type.equalsIgnoreCase(HIGHLIGHT)) {
            obj = validEntity(type, entityId);

            if (Objects.isNull(obj)) {
                response.setError("Invalid Entity  " + type + ", " + entityId);
                return response;
            }
        }

        String fileName = file.getOriginalFilename().trim();
        relativeUrl = trimRelativeUrl(relativeUrl);
        String absoluteUrl = AwsConfig.getMediaBaseUrl() + AwsConfig.getExplorePrefix();
        absoluteUrl = absoluteUrl + (type.equalsIgnoreCase(HIGHLIGHT) ? "" : type);
        if (StringUtils.isNotBlank(relativeUrl)) {
            absoluteUrl = absoluteUrl + DIRECTORY_SEPARATOR_SLASH + relativeUrl;
        }

        InputStream fileInputStream = null;
        try {

            String educationBucket = AwsConfig.getEducationExploreBucketName();
            fileInputStream = file.getInputStream();
            String imagePath = s3Service.uploadFile(
                    fileInputStream, fileName, fileName,
                    type + DIRECTORY_SEPARATOR_SLASH + relativeUrl,
                    educationBucket);

            boolean dataSaved = saveImagePath(obj, imagePath);
            if (Objects.nonNull(obj)) {
                if (dataSaved) {
                    response.setFileName(fileName);
                    response.setFileType(file.getContentType());
                    response.setFileUrl(absoluteUrl + DIRECTORY_SEPARATOR_SLASH + fileName);
                } else {
                    response.setError("Error saving data to db");
                }
            } else {
                if (type.equalsIgnoreCase(HIGHLIGHT)) {
                    response.setFileName(fileName);
                    response.setFileType(file.getContentType());
                    response.setFileUrl(absoluteUrl + DIRECTORY_SEPARATOR_SLASH + fileName);
                }
            }

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

    private boolean saveImagePath(Object obj, String imagePath) {
        if (obj instanceof Institute) {
            log.info("Saving image path for institute to db.");

            Institute institute = (Institute) obj;
            Map<String, Object> queryObject1 = new HashMap<>();
            queryObject1.put(INSTITUTE_ID, institute.getInstituteId());
            String relativeImagePath = getRelativePath(institute.getInstituteId(), imagePath);
            if (StringUtils.isNotBlank(relativeImagePath)) {
                List<String> fields = Arrays.asList(INSTITUTE_ID, GALLERY_LOGO);
                Update update = new Update();
                update.set(GALLERY_LOGO, DIRECTORY_SEPARATOR_SLASH + relativeImagePath);
                commonMongoRepository.upsertData(queryObject1, fields, update,
                        Institute.class);
                return true;
            } else {
                log.info("Error saving data");
            }

        } else if (obj instanceof Exam) {

            log.info("Saving image path for exam to db.");

            Exam exam = (Exam) obj;
            Map<String, Object> queryObject1 = new HashMap<>();
            queryObject1.put(EXAM_ID, exam.getExamId());
            String relativeImagePath = getRelativePath(exam.getExamId(), imagePath);
            if (StringUtils.isNotBlank(relativeImagePath)) {
                List<String> fields = Arrays.asList(EXAM_ID, LOGO);
                Update update = new Update();
                update.set(LOGO, DIRECTORY_SEPARATOR_SLASH + relativeImagePath);
                commonMongoRepository.upsertData(queryObject1, fields, update,
                        Exam.class);
                return true;
            } else {
                log.info("Error saving data");
            }
        } else if (obj instanceof School) {
            log.info("Saving image path for school to db.");

            School school = (School) obj;
            Map<String, Object> queryObject1 = new HashMap<>();
            queryObject1.put(SCHOOL_ID, school.getSchoolId());
            String relativeImagePath = getRelativePath(school.getSchoolId(), imagePath);
            if (StringUtils.isNotBlank(relativeImagePath)) {
                List<String> fields = Arrays.asList(SCHOOL_ID, GALLERY_LOGO);
                Update update = new Update();
                update.set(GALLERY_LOGO, DIRECTORY_SEPARATOR_SLASH + relativeImagePath);
                commonMongoRepository.upsertData(queryObject1, fields, update,
                        School.class);
                return true;
            } else {
                log.info("Error saving data");
            }
        }

        return false;
    }

    private String getRelativePath(Long entityId, String imagePath) {
        int idx = imagePath.indexOf(String.valueOf(entityId));
        if (idx > 0) {
            return imagePath.substring(imagePath.indexOf(String.valueOf(entityId)));
        }
        return null;
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

    private Object validEntity(String type, Long entityId) {
        if (type.equalsIgnoreCase(COLLEGE)) {
            Institute institute = commonMongoRepository
                    .getEntityByFields(INSTITUTE_ID, entityId, Institute.class,
                            Arrays.asList(INSTITUTE_ID, GALLERY_LOGO));

            return institute;
        } else if (type.equalsIgnoreCase(EXAM)) {
            Exam exam = commonMongoRepository
                    .getEntityByFields(EXAM_ID, entityId, Exam.class,
                            Arrays.asList(EXAM_ID, LOGO));
            return exam;
        } else if (type.equalsIgnoreCase(SCHOOL)) {
            School school = commonMongoRepository
                    .getEntityByFields(SCHOOL_ID, entityId, School.class,
                            Arrays.asList(SCHOOL_ID, GALLERY_LOGO));
            return school;
        }
        return null;
    }
}
