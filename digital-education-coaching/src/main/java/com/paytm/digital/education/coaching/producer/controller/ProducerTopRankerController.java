package com.paytm.digital.education.coaching.producer.controller;



import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.producer.model.request.CreateTopRankerRequest;
import com.paytm.digital.education.coaching.producer.model.request.UpdateTopRankerRequest;
import com.paytm.digital.education.coaching.producer.service.TopRankerService;
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
import javax.validation.constraints.NotNull;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;

@RestController
@RequestMapping(COACHING)
public class ProducerTopRankerController {

    @Autowired
    public TopRankerService topRankerService;

    @PostMapping(
            value = V1 + "/admin" + "/top-ranker",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Stores a top ranker",
            notes = "Takes the data from sheet: " + CoachingConstants.TOP_RANKER_GOOGLE_SHEET)
    public ResponseEntity<?> createTopRanker(
            @RequestBody @Valid @NotNull CreateTopRankerRequest request) {

        this.topRankerService.create(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(
            value = V1 + "/admin" + "/top-ranker",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Updates a top ranker",
            notes = "Takes the data from sheet: " + CoachingConstants.TOP_RANKER_GOOGLE_SHEET)
    public ResponseEntity<?> updateTopRanker(
            @RequestBody @Valid @NotNull UpdateTopRankerRequest request) {

        this.topRankerService.update(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
