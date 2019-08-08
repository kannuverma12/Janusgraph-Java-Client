package com.paytm.digital.education.deal.controller;

import com.paytm.digital.education.deal.data.service.StudentDataService;
import com.paytm.digital.education.deal.database.entity.DealsStudentData;
import com.paytm.digital.education.deal.dto.response.VerificationStatusResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
@RequestMapping("/formfbl")
public class StudentDealController {
    private StudentDataService studentDataService;

    @PostMapping("/deal/v1/student")
    public DealsStudentData saveStudentData(
            @RequestBody @Valid DealsStudentData studentData) {
        return studentDataService.addStudentData(studentData);
    }

    @GetMapping("/deal/v1/student")
    public DealsStudentData fetchStudentData(
            @RequestParam("customer_id") @NotNull @Min(1) Long customerId) {
        return studentDataService.fetchStudentData(customerId);
    }

    @GetMapping("/auth/deal/v1/student/status")
    @CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000", "http://fe.paytm.com",
            "http://staging.paytm.com", "http://beta.paytm.com", "http://paytm.com"},
            allowCredentials = "true")
    public VerificationStatusResponse fetchStudentVerificationStatus(
            @RequestHeader("x-user-id") @NotNull @Min(1) Long customerId) {
        return studentDataService.getStudentVerificationResponse(customerId);
    }
}
