package com.paytm.digital.education.coaching.producer.controller;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.producer.model.dto.CoachingExamDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingExamDataRequest;
import com.paytm.digital.education.coaching.producer.service.CoachingExamManagerService;
import com.paytm.digital.education.database.entity.CoachingExamEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(CoachingConstants.URL.COACHING_BASE)
@Api(description = "Coaching Exams Resource APIs")
public class ProducerCoachingExamController {

    @Autowired
    private CoachingExamManagerService coachingExamManagerService;

    @PostMapping(
            value = "/v1/admin/coaching-exams",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Adds a new coaching exam"
    )
    public ResponseEntity<CoachingExamDTO> insertCoachingExam(
            @Valid @RequestBody CoachingExamDataRequest request) {
        return new ResponseEntity<>(coachingExamManagerService.insertCoachingExam(request),
                HttpStatus.OK);
    }

    @PutMapping(
            value = "/v1/admin/coaching-exams",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Updates existing coaching exam"
    )
    public ResponseEntity<CoachingExamDTO> updateCoachingExam(
            @Valid @RequestBody CoachingExamDataRequest request) {
        return new ResponseEntity<>(coachingExamManagerService.updateCoachingExam(request),
                HttpStatus.OK);
    }
}
