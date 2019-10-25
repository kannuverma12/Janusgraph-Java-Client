package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.Section;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SectionRepository extends MongoRepository<Section, String> {
    List<Section> getSectionsByNameIn(Collection<String> name);
}
