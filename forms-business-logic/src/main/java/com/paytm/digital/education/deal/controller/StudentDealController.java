package com.paytm.digital.education.deal.controller;

import com.paytm.digital.education.deal.data.service.StudentDataService;
import com.paytm.digital.education.deal.database.entity.DealsStudentData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Slf4j
@AllArgsConstructor
@RestController
@Validated
@RequestMapping("/formfbl/deal")
public class StudentDealController {
    private StudentDataService studentDataService;

    @PostMapping("/v1/student")
    public DealsStudentData saveStudentData(
            @RequestBody @Valid DealsStudentData studentData) {
        return studentDataService.addStudentData(studentData);
    }

    @GetMapping("/v1/deal/student")
    public DealsStudentData fetchStudentData(
            @RequestParam("customerId") @NotNull @Min(1) Long customerId) {
        return studentDataService.fetchStudentData(customerId);
    }

    @GetMapping("/v1/deal/student/status")
    public DealsStudentData fetchStudentverificationStatus(
            @RequestParam("customerId") @NotNull @Min(1) Long customerId) {
        return studentDataService.fetchStudentData(customerId);
    }
}
