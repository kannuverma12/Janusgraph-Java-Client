package com.paytm.digital.education.explore.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        List<Institute> institutes = instituteRepository.findByImagesUploadedNotIn(Boolean.TRUE);

        if (!CollectionUtils.isEmpty(institutes)) {
            log.info("In uploadImages with institutes count " + institutes.size());
            for (Institute institute : institutes) {

                if (institute.getGallery() != null && institute.getGallery().getImages() != null) {
                    Map<String, List<String>> imagesMap = institute.getGallery().getImages();
                    Map<String, List<String>> s3ImagesMap =
                            new LinkedHashMap<String, List<String>>();
                    for (String key : institute.getGallery().getImages().keySet()) {
                        if (s3ImagesMap.get(key) == null) {
                            s3ImagesMap.put(key, new ArrayList<String>());
                        }
                        if (!CollectionUtils.isEmpty(imagesMap.get(key))) {
                            List<String> imagesUrls = new ArrayList<String>();
                            for (String imageUrl : imagesMap.get(key)) {
                                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                                log.info("In upload image for instituteId {} and fileName {} ",
                                        institute.getInstituteId(), fileName);
                                try {
                                    String s3FilePath = s3Service.uploadFile(imageUrl, fileName,
                                            institute.getInstituteId());
                                    imagesUrls.add(s3FilePath);
                                } catch (Exception e) {
                                    log.error(
                                            "Error caught while uploading image in uploadImages with exception : {}",
                                            e);
                                    FailedImage failedImage =
                                            new FailedImage(institute.getInstituteId(), imageUrl,
                                                    key, e.getStackTrace().toString());
                                    failedImageRepository.save(failedImage);
                                    continue;
                                }

                            }
                            s3ImagesMap.put(key, imagesUrls);
                        }
                    }
                    Gallery gallery = institute.getGallery();
                    gallery.setImages(s3ImagesMap);
                    institute.setGallery(gallery);
                    institute.setImagesUploaded(Boolean.TRUE);
                    log.info("In uploadImages saving institute with id "
                            + institute.getInstituteId());
                    instituteRepository.save(institute);
                }

            }
        }
        log.info("Exited from uploadImages");
    }

    public void uploadFailedImages() {
        log.info("Entered in uploadFailedImages");
        List<FailedImage> failedImages = failedImageRepository.findAll();

        if (!CollectionUtils.isEmpty(failedImages)) {
            log.info("In uploadImages with institutes count " + failedImages.size());
            for (FailedImage failedImage : failedImages) {
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
                    continue;
                }
                log.info("In uploadFailedImages getting institute with id "
                        + failedImage.getInstituteId());
                Institute institute =
                        instituteRepository.findByInstituteId(failedImage.getInstituteId());
                Gallery gallery = institute.getGallery();
                Map<String, List<String>> imagesMap = gallery.getImages();
                List<String> imageUrls = imagesMap.get(failedImage.getType());
                if (imageUrls == null) {
                    imageUrls = new ArrayList<String>();
                }
                imageUrls.add(s3FilePath);
                imagesMap.put(failedImage.getType(), imageUrls);
                gallery.setImages(imagesMap);
                institute.setGallery(gallery);
                log.info("In uploadFailedImages saving institute with id "
                        + institute.getInstituteId());
                instituteRepository.save(institute);
                failedImageRepository.delete(failedImage);
            }
        }
        log.info("Exited from uploadFailedImages");
    }
}
