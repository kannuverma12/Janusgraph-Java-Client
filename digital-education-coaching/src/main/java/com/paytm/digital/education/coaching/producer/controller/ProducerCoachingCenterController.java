package com.paytm.digital.education.coaching.producer.controller;

import com.paytm.digital.education.database.entity.CoachingCenterEntity;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCenterDataRequest;
import com.paytm.digital.education.coaching.producer.service.CoachingCenterServiceNew;
import io.swagger.annotations.Api;
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

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;

@RestController
@RequestMapping(COACHING)
@Api(description = "Coaching Centre Resource APIs. Should be used for physical coaching centers")
public class ProducerCoachingCenterController {

    @Autowired
    private CoachingCenterServiceNew coachingCenterServiceNew;

    @PostMapping(
            value = "/v1/admin/coaching-centers",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CoachingCenterEntity> insertCoachingCenter(
            @Valid @RequestBody CoachingCenterDataRequest request) {
        return new ResponseEntity<>(coachingCenterServiceNew.insertCoachingCenter(request), HttpStatus.OK);
    }

    @PutMapping(
            value = "/v1/admin/coaching-centers",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CoachingCenterEntity> updateCoachingCenter(
            @Valid @RequestBody CoachingCenterDataRequest request) {
        coachingCenterServiceNew.updateCoachingCenter(request);
        return new ResponseEntity<>(coachingCenterServiceNew.updateCoachingCenter(request), HttpStatus.OK);
    }
}
