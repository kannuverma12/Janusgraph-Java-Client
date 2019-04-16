package com.paytm.digital.education.explore.database.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.paytm.digital.education.explore.database.entity.FailedImage;

public interface FailedImageRepository extends MongoRepository<FailedImage, String> {

}
