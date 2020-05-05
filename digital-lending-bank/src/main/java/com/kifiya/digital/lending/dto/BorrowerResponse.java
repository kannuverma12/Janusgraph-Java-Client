package com.kifiya.digital.lending.dto;

import com.kifiya.digital.lending.dao.Borrowers;
import lombok.Data;

import java.util.List;

@Data
public class BorrowerResponse {
  List<Borrowers> borrowers;
}
