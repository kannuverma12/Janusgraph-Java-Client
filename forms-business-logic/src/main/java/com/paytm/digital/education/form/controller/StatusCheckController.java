package com.paytm.digital.education.form.controller;

import com.paytm.digital.education.form.service.StatusCheckService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/formfbl")
public class StatusCheckController {
    private StatusCheckService statusCheckService;

    @GetMapping("/v1/order/statuscheck")
    public ResponseEntity statusCheck(@RequestParam("order_id") String orderId) {
        statusCheckService.updateStatusToFulfilment(orderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
