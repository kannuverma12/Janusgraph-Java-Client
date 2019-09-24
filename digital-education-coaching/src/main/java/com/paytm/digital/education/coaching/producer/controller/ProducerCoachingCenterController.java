package com.paytm.digital.education.coaching.producer.controller;

import com.paytm.digital.education.coaching.producer.model.dto.CoachingCenterDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCenterDataRequest;
import com.paytm.digital.education.coaching.producer.service.CoachingCenterManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;

@Validated
@RestController
@RequestMapping(COACHING)
@Api(description = "Coaching Centre Resource APIs. Should be used for physical coaching centers")
public class ProducerCoachingCenterController {

    @Autowired
    private CoachingCenterManagerService coachingCenterManagerService;

    @PostMapping(
            value = "/v1/admin/coaching-centers",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Adds a new coaching center",
            notes = "Adds new coaching center in the collection of coaching center")
    public ResponseEntity<CoachingCenterDTO> insertCoachingCenter(
            @Valid @RequestBody CoachingCenterDataRequest request) {
        return new ResponseEntity<>(coachingCenterManagerService.insertCoachingCenter(request),
                HttpStatus.OK);
    }

    @PutMapping(
            value = "/v1/admin/coaching-centers",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Updates existing coaching center",
            notes = "Updates existing coaching center in the collection of coaching center")
    public ResponseEntity<CoachingCenterDTO> updateCoachingCenter(
            @Valid @RequestBody CoachingCenterDataRequest request) {
        return new ResponseEntity<>(coachingCenterManagerService.updateCoachingCenter(request),
                HttpStatus.OK);
    }
}
