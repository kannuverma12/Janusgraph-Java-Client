package com.paytm.digital.education.coaching.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/coaching")
public class CoachingController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

}
