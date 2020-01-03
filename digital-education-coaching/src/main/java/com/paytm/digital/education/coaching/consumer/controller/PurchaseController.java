package com.paytm.digital.education.coaching.consumer.controller;

import com.paytm.digital.education.coaching.consumer.model.request.MerchantNotifyRequest;
import com.paytm.digital.education.coaching.consumer.model.request.VerifyRequest;
import com.paytm.digital.education.coaching.consumer.model.response.transactionalflow.MerchantNotifyResponse;
import com.paytm.digital.education.coaching.consumer.model.response.transactionalflow.VerifyResponse;
import com.paytm.digital.education.coaching.consumer.service.transactionalflow.PurchaseService;
import com.paytm.digital.education.exception.PurchaseException;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.COACHING_BASE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.MERCHANT_NOTIFY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.V1;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.URL.VERIFY;

@RestController
@RequestMapping(value = COACHING_BASE + V1)
@Validated
public class PurchaseController {

    private static final Logger log = LoggerFactory.getLogger(PurchaseController.class);

    @Autowired private PurchaseService purchaseService;

    @PostMapping(value = VERIFY)
    public ResponseEntity<VerifyResponse> verify(@Valid @RequestBody VerifyRequest request) {
        try {
            return ResponseEntity.ok(purchaseService.verify(request));
        } catch (PurchaseException e) {
            log.error("Exception occurred in verify call ", e.getCause());
            return new ResponseEntity<>(VerifyResponse.builder()
                    .message(e.getMessage())
                    .acknowledged(e.getAcknowledged())
                    .build(),
                    e.getHttpStatus());
        } catch (Exception e) {
            log.error("Exception occurred in verify call ", e);
            return new ResponseEntity<>(VerifyResponse.builder()
                    .message("Something went wrong")
                    .acknowledged(false)
                    .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = MERCHANT_NOTIFY)
    public MerchantNotifyResponse notify(@Valid @RequestBody MerchantNotifyRequest request) {
        return purchaseService.notify(request);
    }
}
