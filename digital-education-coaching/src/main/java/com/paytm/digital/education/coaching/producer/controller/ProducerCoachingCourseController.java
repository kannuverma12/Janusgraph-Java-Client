package com.paytm.digital.education.coaching.producer.controller;

import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseCreateRequest;
import com.paytm.digital.education.coaching.producer.service.CourseService;
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
public class ProducerCoachingCourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping(
            value = "/v1/coaching/course",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> insertCoachingProgram(
            @Valid @RequestBody CoachingCourseCreateRequest request) {
        courseService.save(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(
            value = "/v1/coaching/course",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCoachingProgram(
            @Valid @RequestBody CoachingCourseCreateRequest request) {
        courseService.update(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
