package com.paytm.digital.education.explore.controller;

import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

import com.paytm.digital.education.explore.daoresult.SubscribedEntityCount;
import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.entity.Subscription;
import com.paytm.digital.education.explore.enums.SubscribableEntityType;
import com.paytm.digital.education.explore.service.CommonMongoService;
import com.paytm.digital.education.explore.service.EntityDetailsService;
import com.paytm.digital.education.explore.service.SubscriptionService;
import com.paytm.digital.education.explore.sro.request.SubscriptionRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(EDUCATION_BASE_URL)
@Slf4j
@Validated
public class ExploreController {

    private SubscriptionService subscriptionService;
    private EntityDetailsService entityDetailsService;
    private CommonMongoService commonMongoService;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @RequestMapping(method = RequestMethod.POST, path = "/auth/v1/subscribe")
    @ResponseBody
    public String subscribe(
        @RequestHeader(name = "x-user-id") @Min(1) long userId,
        @RequestBody SubscriptionRequest request) {
        subscriptionService.subscribe(userId, request.getSubscriptionEntity(),
            request.getSubscriptionEntityId());
        return "success";
    }

    @RequestMapping(method = RequestMethod.GET, path = "/v1/exam/{examId}")
    public @ResponseBody Exam getExamById(@PathVariable("examId") long examId,
            @RequestParam(name = "field_group", required = false) String fieldGroups,
            @RequestParam(name = "fields", required = false) String fields) {
        return entityDetailsService
                .getEntityDetails(EXAM_ID, examId, Exam.class, fieldGroups, fields);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/v1/course/{courseId}")
    public @ResponseBody Course getCourseById(@PathVariable("courseId") long courseId,
            @RequestParam(name = "field_group", required = false) String fieldGroups,
            @RequestParam(name = "fields", required = false) String fields) {
        return entityDetailsService
                .getEntityDetails(COURSE_ID, courseId, Course.class, fieldGroups, fields);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/v1/institute/{instituteId}")
    public @ResponseBody Institute getInstituteById(@PathVariable("instituteId") long instituteId,
            @RequestParam(name = "field_group", required = false) String fieldGroups,
            @RequestParam(name = "fields", required = false) String fields) {
        return entityDetailsService
                .getEntityDetails(INSTITUTE_ID, instituteId, Institute.class, fieldGroups, fields);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/auth/v1/unsubscribe")
    @ResponseBody
    public String unsubscribe(
        @RequestHeader(name = "x-user-id") @Min(1) long userId,
        @RequestBody SubscriptionRequest request) {

        subscriptionService.unsubscribe(userId, request.getSubscriptionEntity(),
            request.getSubscriptionEntityId());
        return "success";
    }

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/subscriptions/count")
    @ResponseBody
    public List<SubscribedEntityCount> subscriptionCount(
        @RequestHeader("x-user-id") @Min(1) long userId,
        @RequestParam("entities") List<SubscribableEntityType> subscribableEntityTypeList) {
        return subscriptionService.fetchSubscribedEntityCount(userId, subscribableEntityTypeList);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/subscriptions")
    @ResponseBody
    public ResponseEntity subscriptions(
        @RequestHeader("x-user-id") @Min(1) long userId,
        @RequestParam(value = "entity") SubscribableEntityType subscriptionEntity,
        @RequestParam(value = "fields", required = false) List<String> fields,
        @RequestParam(value = "field_group", required = false) String fieldGroup,
        @RequestParam(value = "offset", required = false, defaultValue = "0") long offset,
        @RequestParam(value = "limit", required = false, defaultValue = "10") long limit) {

        if (subscriptionService.isFieldsAndFieldGroupParamsInvalid(fields, fieldGroup)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Subscription> userSubscriptionResultList = subscriptionService
            .fetchSubscriptions(userId, subscriptionEntity, fields, fieldGroup, offset, limit);
        return ResponseEntity.ok(userSubscriptionResultList);
    }
}
