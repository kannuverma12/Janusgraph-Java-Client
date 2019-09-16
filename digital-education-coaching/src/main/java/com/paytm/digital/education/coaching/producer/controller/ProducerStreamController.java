package com.paytm.digital.education.coaching.producer.controller;

import com.paytm.digital.education.coaching.producer.model.dto.StreamDTO;
import com.paytm.digital.education.coaching.producer.model.request.StreamDataRequest;
import com.paytm.digital.education.coaching.producer.service.StreamManagerService;
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
@Api(description = "Streams Resource")
public class ProducerStreamController {

    @Autowired
    private StreamManagerService streamManagerService;

    @PostMapping(
            value = "/v1/admin/streams",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Adds a new stream",
            notes = "Adds new stream in the collection of streams")
    public ResponseEntity<StreamDTO> createStream(@Valid @RequestBody StreamDataRequest request) {
        return new ResponseEntity<>(streamManagerService.create(request), HttpStatus.OK);
    }

    @PutMapping(
            value = "/v1/admin/streams",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Updates existing stream",
            notes = "Updates existing stream in the collection of streams")
    public ResponseEntity<StreamDTO> updateStream(@Valid @RequestBody StreamDataRequest request) {
        return new ResponseEntity<>(streamManagerService.update(request), HttpStatus.OK);
    }
}
