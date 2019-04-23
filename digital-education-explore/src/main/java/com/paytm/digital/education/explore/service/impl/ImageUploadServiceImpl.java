package com.paytm.digital.education.explore.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.paytm.digital.education.explore.database.entity.FailedImage;
import com.paytm.digital.education.explore.database.entity.Gallery;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.repository.FailedImageRepository;
import com.paytm.digital.education.explore.database.repository.InstituteRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImageUploadServiceImpl {

    @Autowired
    private InstituteRepository   instituteRepository;

    @Autowired
    private FailedImageRepository failedImageRepository;

    @Autowired
    private S3Service             s3Service;

    public void uploadImages() {
        log.info("Entered in uploadImages");
        List<Institute> institutes = instituteRepository.fetchInstitutesWithoutS3ImagesOrS3Logo();
        Integer imageUploadCount = 0;
        if (!CollectionUtils.isEmpty(institutes)) {
            log.info("In uploadImages with institutes count " + institutes.size());
            for (Institute institute : institutes) {
                Date lastUpdated = institute.getUpdated_at();
                if (institute.getGallery() != null && institute.getGallery().getImages() != null
                        && institute.getGallery().getS3Images() == null) {
                    Map<String, List<String>> imagesMap = institute.getGallery().getImages();
                    Map<String, List<String>> s3ImagesMap =
                            new LinkedHashMap<String, List<String>>();


                    for (String key : imagesMap.keySet()) {
                        if (s3ImagesMap.get(key) == null) {
                            s3ImagesMap.put(key, new ArrayList<String>());
                        }
                        if (!CollectionUtils.isEmpty(imagesMap.get(key))) {
                            List<String> imagesUrls = new ArrayList<String>();
                            for (String imageUrl : imagesMap.get(key)) {
                                if (imageUrl == null || !imageUrl.contains("/")) {
                                    log.error(
                                            "In uploadImages with imageUrl is null or incorrect with institute id {} and imageurl {} ",
                                            institute.getInstituteId(), imageUrl);
                                    continue;
                                }
                                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                                uploadImageToS3(institute, key, imagesUrls, imageUrl,
                                        fileName);
                            }
                            s3ImagesMap.put(key, imagesUrls);
                        }
                    }
                    String s3Logo = "";
                    if (institute.getGallery() != null
                            && StringUtils.isNotBlank(institute.getGallery().getLogo())
                            && institute.getGallery().getS3Logo() == null) {
                        if (!StringUtils.contains(institute.getGallery().getLogo(), "careers360")) {
                            s3Logo = institute.getGallery().getLogo();
                        } else {
                            String fileName = institute.getGallery().getLogo().substring(
                                    institute.getGallery().getLogo().lastIndexOf("/") + 1);
                            s3Logo = uploadImageToS3(institute, "logo", new ArrayList<String>(),
                                    institute.getGallery().getLogo(),
                                    fileName);
                        }
                    }
                    Gallery gallery = institute.getGallery();
                    gallery.setS3Images(s3ImagesMap);
                    gallery.setS3Logo(s3Logo);
                    institute.setGallery(gallery);
                    Institute updatedInstitute =
                            instituteRepository.findByInstituteId(institute.getInstituteId());
                    if (lastUpdated.equals(updatedInstitute.getUpdated_at())) {
                        log.info("In uploadImages saving institute with id "
                                + institute.getInstituteId());
                        instituteRepository.save(institute);
                    } else {
                        log.error(
                                "In  uploadImages update came before saving for id {}, previous updatedAt{} and new upDatedat {} ",
                                institute.getInstituteId(), lastUpdated,
                                updatedInstitute.getLastUpdated());
                    }
                }
            }
        }
        log.info("Exited from uploadImages with imageUploadCount " + imageUploadCount);
    }

    private String uploadImageToS3(Institute institute, String key, List<String> imagesUrls,
            String imageUrl, String fileName) {
        int count = 1;
        int maxTries = 2;
        String s3FilePath = null;
        while (true) {
            try {
                s3FilePath = s3Service.uploadFile(imageUrl, fileName,
                        institute.getInstituteId());
                imagesUrls.add(s3FilePath);
                break;
            } catch (Exception e) {
                if (count < maxTries) {
                    log.error(
                            "Error caught while uploading image in uploadImages with id {} count {} maxTries {} exception  : {}",
                            institute.getInstituteId(), count, maxTries,
                            e);
                    count++;
                } else {
                    log.error(
                            "Error caught while uploading image in uploadImages with id {} exception : {}",
                            institute.getInstituteId(),
                            e);
                    String rootCause =
                            ExceptionUtils.getRootCauseMessage(e);
                    saveFailedImage(institute, key, imageUrl, rootCause);
                    break;
                }
            }
        }
        return s3FilePath;
    }

    private void saveFailedImage(Institute institute, String key, String imageUrl,
            String rootCause) {
        FailedImage failedImage =
                new FailedImage(institute.getInstituteId(),
                        imageUrl,
                        key, rootCause);
        failedImage.setRetryCount(1);
        failedImage.setCreated_at(new Date());
        failedImage
                .setLastUpdatedAt(failedImage.getCreated_at());
        failedImageRepository.save(failedImage);
    }

    public void uploadFailedImages() {
        log.info("Entered in uploadFailedImages");
        List<FailedImage> failedImages = failedImageRepository.findByIsDeletedNotIn(Boolean.TRUE);

        if (!CollectionUtils.isEmpty(failedImages)) {
            log.info("In uploadImages with institutes count " + failedImages.size());
            for (FailedImage failedImage : failedImages) {
                if (failedImage.getRetryCount() == null || failedImage.getRetryCount() < 3) {
                    String fileName =
                            failedImage.getImageUrl()
                                    .substring(failedImage.getImageUrl().lastIndexOf("/") + 1);
                    String s3FilePath = null;
                    try {
                        s3FilePath = s3Service.uploadFile(failedImage.getImageUrl(), fileName,
                                failedImage.getInstituteId());
                    } catch (Exception e) {
                        log.error(
                                "Error caught while uploading image in uploadFailedImages with exception : {}",
                                e);
                        Integer retryCount = failedImage.getRetryCount() + 1;
                        failedImage.setRetryCount(retryCount);
                        failedImage.setLastUpdatedAt(new Date());
                        failedImageRepository.save(failedImage);
                        continue;
                    }
                    log.info("In uploadFailedImages getting institute with id "
                            + failedImage.getInstituteId());
                    Institute institute =
                            instituteRepository.findByInstituteId(failedImage.getInstituteId());
                    Gallery gallery = institute.getGallery();
                    if ("logo".equalsIgnoreCase(failedImage.getType())) {
                        gallery.setS3Logo(s3FilePath);
                    } else {

                        Map<String, List<String>> imagesMap = gallery.getS3Images();
                        List<String> imageUrls = imagesMap.get(failedImage.getType());
                        if (imageUrls == null) {
                            imageUrls = new ArrayList<String>();
                        }
                        imageUrls.add(s3FilePath);
                        imagesMap.put(failedImage.getType(), imageUrls);
                        gallery.setS3Images(imagesMap);
                    }
                    institute.setGallery(gallery);
                    log.info("In uploadFailedImages saving institute with id "
                            + institute.getInstituteId());
                    if (institute.getUpdated_at().compareTo(failedImage.getCreated_at()) < 0) {
                        instituteRepository.save(institute);
                        failedImage.setIsDeleted(Boolean.TRUE);
                        failedImageRepository.save(failedImage);
                    } else {
                        log.error("Institute updated for failedImage with id "
                                + institute.getInstituteId());
                    }
                }
            }
        }
        log.info("Exited from uploadFailedImages");
    }
}
