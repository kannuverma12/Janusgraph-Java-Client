package com.paytm.digital.education.coaching.consumer.controller;


import com.paytm.digital.education.coaching.consumer.model.request.PostMerchantProductsRequest;
import com.paytm.digital.education.coaching.consumer.model.response.CartDataResponse;
import com.paytm.digital.education.coaching.consumer.service.MerchantProductsTransformerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MerchantProductController {

    @Autowired
    private MerchantProductsTransformerService merchantProductsTransformerService;

    @PostMapping("/merchant-products")
    public CartDataResponse postMerchantProducts(@Valid @RequestBody
            PostMerchantProductsRequest request) {
        return merchantProductsTransformerService.getCartDataFromVertical(request);
    }
}
