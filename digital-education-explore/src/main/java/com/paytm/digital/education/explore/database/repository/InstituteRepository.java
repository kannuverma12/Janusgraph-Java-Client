package com.paytm.digital.education.explore.database.repository;

import com.paytm.digital.education.explore.database.entity.Institute;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
@Repository
public interface InstituteRepository extends MongoRepository<Institute, String> {

    Institute findByInstituteId(long id);

    @Query(value = "{'rankings':{'$exists':true,'$ne':[], '$elemMatch':{'source':'NIRF','ranking_type':'OVERALL'}}}",
            fields = "{'institute_id':1,'gallery.logo':1,'rankings':1, 'official_name':1}")
    List<Institute> findAllByNIRFOverallRanking();

}
