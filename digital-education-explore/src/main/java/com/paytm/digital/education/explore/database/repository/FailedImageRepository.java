package com.paytm.digital.education.explore.database.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.paytm.digital.education.explore.database.entity.FailedImage;

public interface FailedImageRepository extends MongoRepository<FailedImage, String> {

    List<FailedImage> findByIsDeletedNotIn(Boolean isDeleted);
    
}
