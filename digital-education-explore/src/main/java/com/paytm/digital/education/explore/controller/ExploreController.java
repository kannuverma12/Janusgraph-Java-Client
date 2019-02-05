package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.explore.database.entity.Subscription;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.service.impl.SubscriptionServiceImpl;
import com.paytm.digital.education.explore.sro.request.SubscriptionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("/v1")
public class ExploreController {

    @Autowired
    private SubscriptionServiceImpl subscriptionServiceImpl;

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
