package com.paytm.digital.education.explore.controller;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.paytm.digital.education.dto.NotificationFlags;
import com.paytm.digital.education.explore.enums.Gender;
import com.paytm.digital.education.explore.response.dto.detail.ExamAndCutOff;
import com.paytm.digital.education.explore.response.dto.detail.ExamInfo;
import com.paytm.digital.education.explore.response.dto.search.CutoffSearchResponse;
import com.paytm.digital.education.explore.service.CutoffService;
import com.paytm.digital.education.explore.service.impl.ExamListServiceImpl;

import com.paytm.digital.education.explore.validators.UrlParamsValidator;
import com.paytm.digital.education.service.notification.NotificationServiceImpl;
import com.paytm.digital.education.utility.JsonUtils;
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
import com.paytm.digital.education.explore.database.entity.Subscription;
import com.paytm.digital.education.explore.enums.SubscribableEntityType;
import com.paytm.digital.education.explore.enums.SubscriptionStatus;
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

    private UrlParamsValidator      urlParamsValidator;
    private SubscriptionService     subscriptionService;
    private CutoffService           cutoffService;
    private ExploreValidator        exploreValidator;
    private ExamListServiceImpl     examListService;
    private NotificationServiceImpl notificationServiceImpl;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @RequestMapping(method = RequestMethod.POST, path = "/auth/v1/subscribe")
    @ResponseBody
    public NotificationFlags subscribe(
            @RequestHeader(name = "x-user-id") @Min(1) long userId,
            @Valid @RequestBody SubscriptionRequest request) {
        log.info("Subscribe Request : {}", JsonUtils.toJson(request));
        return subscriptionService.subscribe(userId, request.getSubscriptionEntity(),
                request.getSubscriptionEntityId());
    }

    @RequestMapping(method = RequestMethod.POST, path = "/auth/v1/unsubscribe")
    @ResponseBody
    public NotificationFlags unsubscribe(
            @RequestHeader(name = "x-user-id") @Min(1) long userId,
            @RequestBody @Valid SubscriptionRequest request) {
        log.info("Unsubscribe Request : {}", JsonUtils.toJson(request));
        return subscriptionService.unsubscribe(userId, request.getSubscriptionEntity(),
                request.getSubscriptionEntityId());
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

    @RequestMapping(method = RequestMethod.GET, path = "/v1/cutoffs/search")
    public List<CutoffSearchResponse> searchCutOffs(
            @RequestParam(value = "institute_id") @Min(1) long instituteId,
            @RequestParam(value = "institute_name") @NotBlank String instituteName,
            @RequestParam(value = "exam_id") @Min(1) long examId,
            @RequestParam(value = "exam_name") @NotBlank String examName,
            @RequestParam(value = "gender", required = false) Gender gender,
            @RequestParam(value = "caste_group", required = false) String casteGroup,
            @RequestParam(value = "field_group") @NotNull String fieldGroup) {
        urlParamsValidator.validateInstuteUrlKey(instituteId, instituteName);
        urlParamsValidator.validateExamUrlKey(examId, examName);
        exploreValidator.validateFieldAndFieldGroup(null, fieldGroup);
        return cutoffService
                .searchCutOffs(instituteId, examId, gender, casteGroup, fieldGroup);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/v1/cutoffs/search_list")
    public ExamAndCutOff getList(
            @RequestParam(value = "institute_id") @Min(1) long instituteId,
            @RequestParam(value = "institute_name") @NotBlank String instituteName,
            @RequestParam(value = "exam_id") @Min(1) long examId,
            @RequestParam(value = "exam_name") @NotBlank String examName) {
        urlParamsValidator.validateInstuteUrlKey(instituteId, instituteName);
        urlParamsValidator.validateExamUrlKey(examId, examName);
        return cutoffService.getSearchList(instituteId, examId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/v1/institute/exam_list/{instituteId}/{instituteName}")
    public List<ExamInfo> getExamList(@PathVariable("instituteId") @Min(1) long instituteId,
            @PathVariable("instituteName") @NotBlank String instituteName) {
        urlParamsValidator.validateInstuteUrlKey(instituteId, instituteName);
        return examListService.getExamList(instituteId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/user_flags")
    @ResponseBody
    public NotificationFlags getUserFlags(
            @RequestHeader(value = "x-user-id") @Min(1) @NotNull Long userId) {
        return notificationServiceImpl.getNotificationFlags(userId);
    }
}
