package com.paytm.digital.education.deal.controller;

import com.paytm.digital.education.deal.data.service.StudentDataService;
import com.paytm.digital.education.deal.database.entity.DealsEligibleStudentData;
import com.paytm.digital.education.deal.response.dto.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/v1/deals")
@Slf4j
@AllArgsConstructor
@RestController
@Validated

public class StudentDealController {
    private StudentDataService studentDataService;

    @PostMapping("/add/student-data")
    public ResponseDto createInstitute(
            @RequestBody @Valid DealsEligibleStudentData studentData) {
        return studentDataService.addStudentData(studentData);
    }
}
