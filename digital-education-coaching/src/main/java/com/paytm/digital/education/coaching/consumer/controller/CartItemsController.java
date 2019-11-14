package com.paytm.digital.education.coaching.consumer.controller;


import com.paytm.digital.education.coaching.consumer.model.request.FetchCartItemsRequestBody;
import com.paytm.digital.education.coaching.consumer.model.response.transactionalflow.CartDataResponse;
import com.paytm.digital.education.coaching.consumer.service.transactionalflow.MerchantProductsTransformerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.COACHING_BASE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;

@Slf4j
@RestController
@RequestMapping(value = COACHING_BASE + V1)
@Validated
public class CartItemsController {

    @Autowired
    private MerchantProductsTransformerService merchantProductsTransformerService;

    @PostMapping("/fetch-cart-items")
    public CartDataResponse fetchCartItems(@Valid @RequestBody
            FetchCartItemsRequestBody request) {
        return merchantProductsTransformerService.fetchCartDataFromVertical(request);
    }
}
