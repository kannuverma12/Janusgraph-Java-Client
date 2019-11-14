package com.paytm.digital.education.coaching.consumer.controller;

import com.paytm.digital.education.coaching.consumer.model.request.CheckoutDataRequest;
import com.paytm.digital.education.coaching.consumer.model.response.transactionalflow.CheckoutDataResponse;
import com.paytm.digital.education.coaching.consumer.service.transactionalflow.CheckoutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.CHECKOUT_DATA;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.COACHING_BASE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;

@Slf4j
@RestController
@RequestMapping(value = COACHING_BASE + V1)
@Validated
public class CheckoutController {

    @Autowired CheckoutService checkoutService;

    @PostMapping(value = CHECKOUT_DATA)
    public CheckoutDataResponse checkoutData(@Valid @RequestBody CheckoutDataRequest request) {
        return checkoutService.checkoutData(request);
    }
}
