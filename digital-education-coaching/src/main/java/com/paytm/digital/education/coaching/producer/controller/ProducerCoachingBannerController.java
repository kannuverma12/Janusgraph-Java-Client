package com.paytm.digital.education.coaching.producer.controller;

import com.paytm.digital.education.coaching.producer.model.dto.CoachingBannerDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingBannerDataRequest;
import com.paytm.digital.education.coaching.producer.service.CoachingBannerMangerService;
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
@Api(description = "Coaching Banner Resource APIs")
public class ProducerCoachingBannerController {

    @Autowired
    private CoachingBannerMangerService coachingBannerMangerService;

    @PostMapping(
            value = "/v1/admin/coaching-banner",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "creates a coaching banner",
            notes = "Adds new banner in the coaching banner collection")
    public ResponseEntity<CoachingBannerDTO> createCoachingBanner(
            @Valid @RequestBody CoachingBannerDataRequest request) {
        return new ResponseEntity<>(coachingBannerMangerService.create(request), HttpStatus.OK);
    }

    @PutMapping(
            value = "/v1/admin/coaching-banner",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "update existing coaching banner",
            notes = "update coaching banner in the coaching banner collection")
    public ResponseEntity<CoachingBannerDTO> updateCoachingBanner(
            @Valid @RequestBody CoachingBannerDataRequest request) {
        return new ResponseEntity<>(coachingBannerMangerService.update(request), HttpStatus.OK);
    }

}
