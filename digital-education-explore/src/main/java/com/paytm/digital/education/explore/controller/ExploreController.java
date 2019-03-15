package com.paytm.digital.education.explore.controller;

import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
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
import com.paytm.digital.education.explore.daoresult.SubscribedEntityCount;
import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.Subscription;
import com.paytm.digital.education.explore.enums.SubscribableEntityType;
import com.paytm.digital.education.explore.enums.SubscriptionStatus;
import com.paytm.digital.education.explore.service.EntityDetailsService;
import com.paytm.digital.education.explore.service.SubscriptionService;
import com.paytm.digital.education.explore.sro.request.FetchSubscriptionsRequest;
import com.paytm.digital.education.explore.sro.request.SubscriptionRequest;
import com.paytm.digital.education.explore.validators.ExploreValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@RestController
@RequestMapping(EDUCATION_BASE_URL)
@Slf4j
@Validated
public class ExploreController {
    private static final SubscriptionStatus SUBSCRIBED_STATUS = SubscriptionStatus.SUBSCRIBED;

    private SubscriptionService             subscriptionService;
    private EntityDetailsService            entityDetailsService;
    private ExploreValidator                exploreValidator;
    
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @RequestMapping(method = RequestMethod.POST, path = "/auth/v1/subscribe")
    @ResponseBody
    public String subscribe(
            @RequestHeader(name = "x-user-id") @Min(1) long userId,
            @Valid @RequestBody SubscriptionRequest request) {
        subscriptionService.subscribe(userId, request.getSubscriptionEntity(),
                request.getSubscriptionEntityId());
        return "success";
    }

    @RequestMapping(method = RequestMethod.GET, path = "/v1/course/{courseId}")
    public @ResponseBody Course getCourseById(@PathVariable("courseId") @Min(1) Long courseId,
            @RequestParam(name = "field_group", required = false) String fieldGroup,
            @RequestParam(name = "fields", required = false) List<String> fields) {

        exploreValidator.validateFieldAndFieldGroup(fields, fieldGroup);
        return entityDetailsService
                .getEntityDetails(COURSE_ID, courseId, Course.class, fieldGroup, fields);
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
        return subscriptionService.fetchSubscribedEntityCount(userId, subscribableEntityTypeList,
                SUBSCRIBED_STATUS);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/subscriptions")
    @ResponseBody
    public ResponseEntity subscriptions(
            @RequestHeader(value = "x-user-id") Long userId,
            @RequestParam(value = "entity", required = false)
                    SubscribableEntityType subscriptionEntity,
            @RequestParam(value = "fields", required = false) List<String> fields,
            @RequestParam(value = "field_group", required = false) String fieldGroup,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Long offset,
            @RequestParam(value = "limit", required = false, defaultValue = "10") Long limit) {

        exploreValidator.validateFetchSubscriptionRequest(new FetchSubscriptionsRequest(
                userId,
                subscriptionEntity,
                offset,
                limit,
                fields,
                fieldGroup));

        List<Subscription> userSubscriptionResultList = subscriptionService
                .fetchSubscriptions(
                        userId, subscriptionEntity, fields, fieldGroup, offset, limit,
                        SUBSCRIBED_STATUS);
        return ResponseEntity.ok(userSubscriptionResultList);

    }
}
