package com.kifiya.digital.lending.service.impl;

import com.kifiya.digital.lending.dao.Borrowers;
import com.kifiya.digital.lending.dto.BorrowerResponse;
import com.kifiya.digital.lending.repository.BorrowerRepository;
import com.kifiya.digital.lending.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class BankServiceImpl implements BankService {

  @Autowired
  private BorrowerRepository borrowerRepository;

  @Override
  public BorrowerResponse getBorrowers(Long id) {
    List<Borrowers> borrowerList = borrowerRepository.findByBankId(id);
    BorrowerResponse borrowerResponse = new BorrowerResponse();
    if (!CollectionUtils.isEmpty(borrowerList)) {
      borrowerResponse.setBorrowers(borrowerList);
    }
    return borrowerResponse;
  }
}
