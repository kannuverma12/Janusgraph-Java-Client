package com.kifiya.digital.lending.dao;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document
public class Borrowers {

  @Id
  Long id;

  @Field("first_name")
  String firstName;

  @Field("last_name")
  String lastName;

  @Field("address")
  String address;

  @Field("bank_id")
  Long bankId;

}
