package com.paytm.digital.education.explore.controller;

import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;

import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.entity.Subscription;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.service.impl.EntityDetailsService;
import com.paytm.digital.education.explore.service.impl.SubscriptionServiceImpl;
import com.paytm.digital.education.explore.sro.request.SubscriptionRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/v1")
public class ExploreController {

    private SubscriptionServiceImpl subscriptionServiceImpl;
    private EntityDetailsService    entityDetailsService;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @RequestMapping(method = RequestMethod.POST, path = "/subscribe")
    @ResponseBody
    public String subscribe(
            @RequestBody SubscriptionRequest request) {
        subscriptionServiceImpl.subscribe(request.getUserId(), request.getSubscriptionEntity(),
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
        subscriptionServiceImpl.unsubscribe(request.getUserId(), request.getSubscriptionEntity(),
                request.getSubscriptionEntityId());
        return "success";
    }

    @RequestMapping(method = RequestMethod.GET, path = "/subscriptions")
    @ResponseBody
    public List<Subscription> subscriptions(
            @RequestParam("user_id") long userId,
            @RequestParam(value = "entities",
                    required = false) List<EducationEntity> subscriptionEntitiesList) {
        return subscriptionServiceImpl.fetchSubscriptionList(userId, subscriptionEntitiesList);
    }



}
