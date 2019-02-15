package com.paytm.digital.education.explore.controller;

import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/v1")
@Slf4j
public class ExploreController {

    private SubscriptionService subscriptionService;
    private EntityDetailsService entityDetailsService;
    private CommonMongoService commonMongoService;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @RequestMapping(method = RequestMethod.POST, path = "/subscribe")
    @ResponseBody
    public String subscribe(
        @RequestBody SubscriptionRequest request) {
        subscriptionService.subscribe(request.getUserId(), request.getSubscriptionEntity(),
            request.getSubscriptionEntityId());
        return "success";
    }

    @RequestMapping(method = RequestMethod.GET, path = "/exam/{examId}")
    public @ResponseBody Exam getExamById(@PathVariable("examId") Long examId,
            @RequestParam(name = "field_group", required = false) String fieldGroups,
            @RequestParam(name = "fields", required = false) String fields) {
        return entityDetailsService
                .getEntityDetails(EXAM_ID, examId, Exam.class, fieldGroups, fields);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/course/{courseId}")
    public @ResponseBody Course getCourseById(@PathVariable("courseId") Long courseId,
            @RequestParam(name = "field_group", required = false) String fieldGroups,
            @RequestParam(name = "fields", required = false) String fields) {
        return entityDetailsService
                .getEntityDetails(COURSE_ID, courseId, Course.class, fieldGroups, fields);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/institute/{instituteId}")
    public @ResponseBody Institute getInstituteById(@PathVariable("instituteId") Long instituteId,
            @RequestParam(name = "field_group", required = false) String fieldGroups,
            @RequestParam(name = "fields", required = false) String fields) {
        return entityDetailsService
                .getEntityDetails(INSTITUTE_ID, instituteId, Institute.class, fieldGroups, fields);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/unsubscribe")
    @ResponseBody
    public String unsubscribe(
        @RequestBody SubscriptionRequest request) {

        subscriptionService.unsubscribe(request.getUserId(), request.getSubscriptionEntity(),
            request.getSubscriptionEntityId());
        return "success";
    }

    @RequestMapping(method = RequestMethod.GET, path = "/subscriptions/count")
    @ResponseBody
    public List<SubscribedEntityCount> subscriptionCount(
        @RequestParam("user_id") long userId,
        @RequestParam("entities") List<SubscribableEntityType> subscribableEntityTypeList) {
        return subscriptionService.fetchSubscribedEntityCount(userId, subscribableEntityTypeList);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/subscriptions")
    @ResponseBody
    public ResponseEntity subscriptions(
        @RequestParam("user_id") long userId,
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
