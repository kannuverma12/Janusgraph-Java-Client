package com.paytm.digital.education.application.explore.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/explore")
public class ExploreController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

}
