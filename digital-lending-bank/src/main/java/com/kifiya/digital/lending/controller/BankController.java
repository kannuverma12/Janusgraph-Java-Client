package com.kifiya.digital.lending.controller;

import com.kifiya.digital.lending.dto.BorrowerResponse;
import com.kifiya.digital.lending.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/bank")
public class BankController {

  @Autowired
  private BankService bankService;

  @GetMapping("/details")
  public BorrowerResponse getAllBorrowers(@RequestParam Long bankId) {
    return bankService.getBorrowers(bankId);
  }
}
