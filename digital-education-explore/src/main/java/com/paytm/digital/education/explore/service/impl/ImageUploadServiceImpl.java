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
import com.paytm.digital.education.explore.constants.ExploreConstants;
import com.paytm.digital.education.explore.database.entity.Alumni;
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
    
    int count = 1;
    int maxTries = 2;

    public void uploadImages() {
        log.info("Entered in uploadImages");
        List<Institute> institutes = instituteRepository.fetchInstitutesWithoutS3ImagesOrS3Logo();
        Integer imageUploadCount = 0;
        if (!CollectionUtils.isEmpty(institutes)) {
            log.info("In uploadImages with institutes count " + institutes.size());
            for (Institute institute : institutes) {
                Date lastUpdated = institute.getUpdatedAt();
                if (institute.getGallery() != null) {
                    Map<String, List<String>> s3ImagesMap =
                            new LinkedHashMap<String, List<String>>();
                    //Upload gallery images
                    if (institute.getGallery().getImages() != null
                            && institute.getGallery().getS3Images() == null) {
                    uploadGalleryImages(institute, s3ImagesMap);
                    }
                    
                    //Upload logo
                    String s3Logo = "";
                    if (institute.getGallery() != null
                            && StringUtils.isNotBlank(institute.getGallery().getLogo())
                            && institute.getGallery().getS3Logo() == null) {
                        s3Logo = uploadLogo(institute);
                    }
                    Gallery gallery = institute.getGallery();
                    gallery.setS3Images(s3ImagesMap);
                    gallery.setS3Logo(s3Logo);
                    institute.setGallery(gallery);

                }
                //Upload alumni images
                if (!CollectionUtils.isEmpty(institute.getNotableAlumni())) {
                    uploadAlumniImages(institute);
                }
                Institute updatedInstitute =
                        instituteRepository.findByInstituteId(institute.getInstituteId());
                if (lastUpdated.equals(updatedInstitute.getUpdatedAt())) {
                    log.info("In uploadImages saving institute with id "
                            + institute.getInstituteId());
                    instituteRepository.save(institute);
                } else {
                    log.error(
                            "In  uploadImages update came before saving"
                                    + " for id {}, previous updatedAt{} and new upDatedat {} ",
                            institute.getInstituteId(), lastUpdated,
                            updatedInstitute.getLastUpdated());
                }
            }
        }
        log.info("Exited from uploadImages with imageUploadCount " + imageUploadCount);
    }

    private void uploadGalleryImages(Institute institute, Map<String, List<String>> s3ImagesMap) {
        Map<String, List<String>> imagesMap = institute.getGallery().getImages();
        for (String key : imagesMap.keySet()) {
            if (s3ImagesMap.get(key) == null) {
                s3ImagesMap.put(key, new ArrayList<String>());
            }
            if (!CollectionUtils.isEmpty(imagesMap.get(key))) {
                List<String> imagesUrls = new ArrayList<String>();
                for (String imageUrl : imagesMap.get(key)) {
                    if (imageUrl == null || !imageUrl.contains("/")) {
                        log.error(
                                "In uploadImages with imageUrl is null or incorrect with institute id"
                                        + " {} and imageurl {} ",
                                institute.getInstituteId(), imageUrl);
                        continue;
                    }
                    String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                   String s3FilePath = uploadImageToS3(institute.getInstituteId(), key, imageUrl,
                            fileName);
                    imagesUrls.add(s3FilePath);
                }
                s3ImagesMap.put(key, imagesUrls);
            }
        }
    }

    private String uploadLogo(Institute institute) {
        String s3Logo;
        if (!StringUtils.containsIgnoreCase(institute.getGallery().getLogo(),
                ExploreConstants.CAREERS_360)) {
            s3Logo = institute.getGallery().getLogo();
        } else {
            String fileName = institute.getGallery().getLogo().substring(
                    institute.getGallery().getLogo().lastIndexOf("/") + 1);
            s3Logo = uploadImageToS3(institute.getInstituteId(), ExploreConstants.LOGO,
                    institute.getGallery().getLogo(),
                    fileName);
        }
        return s3Logo;
    }

    private void uploadAlumniImages(Institute institute) {
        List<Alumni> alumnis = institute.getNotableAlumni();
        for (Alumni alumni : alumnis) {
            String s3FilePath = "";
            if (alumni != null && StringUtils.isNotBlank(alumni.getAlumniPhoto())
                    && alumni.getS3AlumniPhoto() == null) {
                String fileName = alumni.getAlumniPhoto()
                        .substring(alumni.getAlumniPhoto().lastIndexOf("/") + 1);
                s3FilePath = uploadImageToS3(institute.getInstituteId(), ExploreConstants.ALUMNI,
                         alumni.getAlumniPhoto(), fileName);
            }
            alumni.setS3AlumniPhoto(s3FilePath);
            institute.setNotableAlumni(alumnis);
        }
    }

    private String uploadImageToS3(Long instituteId, String key,
            String imageUrl, String fileName) {
     
        String s3FilePath = null;
        while (true) {
            try {
                s3FilePath = s3Service.uploadFile(imageUrl, fileName,
                        instituteId);
                break;
            } catch (Exception e) {
                if (count < maxTries) {
                    log.error(
                            "Error caught while uploading image in"
                                    + " uploadImages with id {} count {} maxTries {} exception  : {}",
                                    instituteId, count, maxTries,
                            e);
                    count++;
                } else {
                    log.error(
                            "Error caught while uploading image in uploadImages with id {} exception : {}",
                            instituteId,
                            e);
                    String rootCause =
                            ExceptionUtils.getRootCauseMessage(e);
                    saveFailedImage(instituteId, key, imageUrl, rootCause);
                    break;
                }
            }
        }
        return s3FilePath;
    }

    private void saveFailedImage(Long instituteId, String key, String imageUrl,
            String rootCause) {
        FailedImage failedImage =
                new FailedImage(instituteId,
                        imageUrl,
                        key, rootCause);
        failedImage.setRetryCount(1);
        failedImage.setCreatedAt(new Date());
        failedImage
                .setLastUpdatedAt(failedImage.getCreatedAt());
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
                    if (ExploreConstants.LOGO.equalsIgnoreCase(failedImage.getType())) {
                        gallery.setS3Logo(s3FilePath);
                    } else if (ExploreConstants.ALUMNI.equalsIgnoreCase(failedImage.getType())) {
                        List<Alumni> alumnis = institute.getNotableAlumni();
                        for (Alumni alumni : alumnis) {
                            if (alumni != null && alumni.getAlumniPhoto() != null
                                    && alumni.getAlumniPhoto().equals(failedImage.getImageUrl())) {
                                alumni.setS3AlumniPhoto(s3FilePath);
                            }
                        }
                        institute.setNotableAlumni(alumnis);
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
                    if (institute.getUpdatedAt().compareTo(failedImage.getCreatedAt()) < 0) {
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
