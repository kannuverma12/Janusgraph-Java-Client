package com.paytm.digital.education.form.controller;

import com.paytm.digital.education.form.request.PaymentPostingRequest;
import com.paytm.digital.education.form.service.PaymentPostingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/formfbl")
public class PaymentPostingController {
    private PaymentPostingService paymentPostingService;

    @PostMapping("/v1/paymentPosting")
    public ResponseEntity paymentPosting(@Valid @RequestBody PaymentPostingRequest paymentPostingRequest) {
        // TODO: Send response before processing
        // TODO: Add alert support in case we receive multiple items, coding done for single item currently
        // TODO: To add support for automatic saving of transaction history
        boolean paymentStatus = paymentPostingService.processPaymentPosting(paymentPostingRequest);
        if (paymentStatus) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
}
