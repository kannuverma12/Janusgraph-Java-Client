package com.paytm.digital.education.coaching.producer.controller;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.producer.model.dto.TopRankerDTO;
import com.paytm.digital.education.coaching.producer.model.request.TopRankerDataRequest;
import com.paytm.digital.education.coaching.producer.service.TopRankerManagerService;
import com.paytm.digital.education.database.entity.TopRankerEntity;
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
import javax.validation.constraints.NotNull;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;

@RestController
@Api(description = "Top Ranker Resource APIs")
@RequestMapping(CoachingConstants.URL.COACHING_BASE)
public class ProducerTopRankerController {

    @Autowired
    public TopRankerManagerService topRankerManagerService;

    @PostMapping(
            value = V1 + "/top-ranker",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Stores a top ranker",
            notes = "Takes the data from sheet: " + CoachingConstants.TOP_RANKER_GOOGLE_SHEET)
    public ResponseEntity<TopRankerDTO> createTopRanker(
            @RequestBody @Valid @NotNull TopRankerDataRequest request) {
        return new ResponseEntity<>(topRankerManagerService.create(request), HttpStatus.OK);
    }

    @PutMapping(
            value = V1  + "/top-ranker",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Updates a top ranker",
            notes = "Takes the data from sheet: " + CoachingConstants.TOP_RANKER_GOOGLE_SHEET)
    public ResponseEntity<TopRankerDTO> updateTopRanker(
            @RequestBody @Valid @NotNull TopRankerDataRequest request) {
        return new ResponseEntity<>(topRankerManagerService.update(request), HttpStatus.OK);
    }
}
