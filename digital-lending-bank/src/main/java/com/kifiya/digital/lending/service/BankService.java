package com.kifiya.digital.lending.service;

import com.kifiya.digital.lending.dto.BorrowerResponse;

public interface BankService {
  BorrowerResponse getBorrowers(Long bankId);
}
