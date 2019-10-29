package com.paytm.digital.education.coaching.producer.controller;

import com.paytm.digital.education.coaching.producer.model.dto.CoachingCourseFeatureDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseFeatureDataRequest;
import com.paytm.digital.education.coaching.producer.service.CoachingCourseFeatureManagerService;
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
@Api(description = "Coaching Course Feature Resource APIs")
public class ProducerCoachingCourseFeatureController {

    @Autowired
    private CoachingCourseFeatureManagerService coachingCourseFeatureManagerService;

    @PostMapping(
            value = "/v1/admin/coaching-course-feature",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "creates a coaching course feature",
            notes = "Adds new banner in the coaching course feature collection")
    public ResponseEntity<CoachingCourseFeatureDTO> createCoachingCourseFeature(
            @Valid @RequestBody CoachingCourseFeatureDataRequest request) {
        return new ResponseEntity<>(coachingCourseFeatureManagerService.create(request),
                HttpStatus.OK);
    }

    @PutMapping(
            value = "/v1/admin/coaching-course-feature",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "update existing coaching course feature",
            notes = "update coaching course feature in the coaching banner collection")
    public ResponseEntity<CoachingCourseFeatureDTO> updateCoachingCourseFeature(
            @Valid @RequestBody CoachingCourseFeatureDataRequest request) {
        return new ResponseEntity<>(coachingCourseFeatureManagerService.update(request),
                HttpStatus.OK);
    }

}

