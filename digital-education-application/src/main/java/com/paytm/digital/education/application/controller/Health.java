package com.paytm.digital.education.application.controller;

import com.paytm.digital.education.application.model.contoller.HealthResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/health")
public class Health {

    @GetMapping("/check")
    public HealthResponse getHealth() {
        return new HealthResponse("OK");
    }

}
