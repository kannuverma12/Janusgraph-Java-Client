package com.paytm.digital.education.coaching.consumer.controller;

import com.paytm.digital.education.coaching.consumer.model.request.FetchCartItemsRequestBody;
import com.paytm.digital.education.coaching.consumer.model.response.transactionalflow.CartDataResponse;
import com.paytm.digital.education.coaching.consumer.service.transactionalflow.MerchantProductsTransformerService;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.COACHING_BASE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;

@RestController
@RequestMapping(value = COACHING_BASE + V1)
@Validated
public class CartItemsController {

    private static final Logger log = LoggerFactory.getLogger(CartItemsController.class);

    @Autowired
    private MerchantProductsTransformerService merchantProductsTransformerService;

    @PostMapping("/fetch-cart-items")
    public CartDataResponse fetchCartItems(@Valid @RequestBody
            FetchCartItemsRequestBody request) {
        return merchantProductsTransformerService.fetchCartDataFromVertical(request);
    }
}
