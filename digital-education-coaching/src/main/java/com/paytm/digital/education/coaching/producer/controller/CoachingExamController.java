package com.paytm.digital.education.coaching.producer.controller;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.producer.model.request.CoachingExamCreateRequest;
import com.paytm.digital.education.coaching.producer.model.request.CoachingExamUpdateRequest;
import com.paytm.digital.education.coaching.producer.service.CoachingExamServiceNew;
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
public class CoachingExamController {

    @Autowired
    CoachingExamServiceNew coachingExamServiceNew;

    @PostMapping(
            value = "/v1/admin/coaching-exam",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> insertCoachingExam(
            @Valid @RequestBody CoachingExamCreateRequest request) {
        coachingExamServiceNew.insertCoachingExam((request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(
            value = "/v1/admin/coaching-exam",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCoachingExam(
            @Valid @RequestBody CoachingExamUpdateRequest request) {
        coachingExamServiceNew.updateCoachingExam((request));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
