package com.kifiya.digital.lending.repository;

import com.kifiya.digital.lending.dao.Borrowers;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowerRepository extends MongoRepository<Borrowers, ObjectId> {
  List<Borrowers> findByBankId(Long bankId);
}
