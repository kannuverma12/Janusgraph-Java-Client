package com.paytm.digital.education.explore.database.repository;

import com.paytm.digital.education.explore.database.entity.Institute;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Repository
public interface InstituteRepository extends MongoRepository<Institute, String> {

    Institute findByInstituteId(long id);
    
    @Query("{'$or':[ {'gallery.s3Images' : {$exists:false}}, {'gallery.s3Logo' : {$exists:false}}]}")
    List<Institute> fetchInstitutesWithoutS3ImagesOrS3Logo();
}
