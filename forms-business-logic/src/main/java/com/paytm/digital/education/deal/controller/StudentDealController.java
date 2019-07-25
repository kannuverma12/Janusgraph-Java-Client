package com.paytm.digital.education.deal.controller;

import com.paytm.digital.education.deal.data.service.StudentDataService;
import com.paytm.digital.education.deal.database.entity.DealsEligibleStudentData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RequestMapping("/formfbl")
@Slf4j
@AllArgsConstructor
@RestController
@Validated

public class StudentDealController {
    private StudentDataService studentDataService;

    @PostMapping("/v1/deal/student")
    public DealsEligibleStudentData saveStudentData(
            @RequestBody @Valid DealsEligibleStudentData studentData) {
        return studentDataService.addStudentData(studentData);
    }

    @GetMapping("/v1/deal/student/{customerId}")
    public DealsEligibleStudentData fetchStudentData(@PathVariable @NotNull @Min(1) Long customerId) {
        return studentDataService.fetchStudentData(customerId);
    }
}
