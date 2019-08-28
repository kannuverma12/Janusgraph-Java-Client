package com.paytm.digital.education.coaching.producer.controller;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.coaching.producer.model.request.CoachingInstituteDataRequest;
import com.paytm.digital.education.coaching.producer.service.CoachingInstituteService;
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

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;

@RestController
@RequestMapping(COACHING)
@Api(description = "Coaching Institute Resource. Keeps governing body as resource.")
public class ProducerCoachingInstituteController {

    @Autowired
    private CoachingInstituteService coachingInstituteService;

    @PostMapping(
            value = "/v1/admin/institutes",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "creates an institute",
            notes = "takes the data from sheet : " + CoachingConstants.INSTITUTES_GOOGLE_SHEET)
    public ResponseEntity<CoachingInstituteEntity> createInstitute(
            @Valid @RequestBody CoachingInstituteDataRequest request) {
        return new ResponseEntity<>(coachingInstituteService.create(request), HttpStatus.OK);
    }

    @PutMapping(
            value = "/v1/admin/institutes",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "updates existing institute",
            notes = "takes the data from sheet : " + CoachingConstants.INSTITUTES_GOOGLE_SHEET)
    public ResponseEntity<CoachingInstituteEntity> updateInstitute(
            @Valid @RequestBody CoachingInstituteDataRequest request) {
        return new ResponseEntity<>(coachingInstituteService.update(request), HttpStatus.OK);
    }
}
