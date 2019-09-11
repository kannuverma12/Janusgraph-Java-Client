package com.paytm.digital.education.coaching.producer.controller;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.producer.model.dto.TargetExamDTO;
import com.paytm.digital.education.coaching.producer.model.request.TargetExamUpdateRequest;
import com.paytm.digital.education.coaching.producer.service.TargetExamManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;

@RestController
@Api(description = "Target Exam Resource APIs")
@RequestMapping(CoachingConstants.URL.COACHING_BASE)
public class ProducerTargetExamController {

    @Autowired
    public TargetExamManagerService targetExamManagerService;

    @PatchMapping(
            value = V1 + "/exam",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "upsert an existing exam data")
    public ResponseEntity<TargetExamDTO> updateExam(
            @RequestBody @Valid @NotNull TargetExamUpdateRequest request) {
        return new ResponseEntity<>(targetExamManagerService.update(request), HttpStatus.OK);
    }
}
