package com.paytm.digital.education.coaching.producer.controller;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.producer.model.dto.CoachingCourseDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseDataRequest;
import com.paytm.digital.education.coaching.producer.service.CoachingCourseManagerService;
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
@Api(description = "Coaching Program Resource APIs")
public class ProducerCoachingCourseController {

    @Autowired
    private CoachingCourseManagerService coachingCourseManagerService;

    @PostMapping(
            value = "/v1/coaching-programs",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds a new coaching program",
            notes = "Adds new coaching program in the collection of programs")
    public ResponseEntity<CoachingCourseDTO> insertCoachingProgram(
            @Valid @RequestBody CoachingCourseDataRequest request) {
        return new ResponseEntity<>(coachingCourseManagerService.save(request), HttpStatus.OK);
    }

    @PutMapping(
            value = "/v1/coaching-programs",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates existing coaching program",
            notes = "Updates existing coaching program in the collection of programs")
    public ResponseEntity<CoachingCourseDTO> updateCoachingProgram(
            @Valid @RequestBody CoachingCourseDataRequest request) {
        return new ResponseEntity<>(coachingCourseManagerService.update(request), HttpStatus.OK);
    }
}
