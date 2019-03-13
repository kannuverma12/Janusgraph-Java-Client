package com.paytm.digital.education.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.GitProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @Autowired(required = false)
    private GitProperties gitProperties;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version}")
    private String applicationVersion;

    @GetMapping("/health")
    public ResponseEntity health() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Date startDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        Map<String, Object> result = new HashMap<>();
        result.put("program", applicationName);
        result.put("version", applicationVersion);
        result.put("release", gitProperties != null ? gitProperties.getCommitId() : null);
        result.put("datetime", format.format(startDate));
        result.put("timestamp", startDate.getTime());
        result.put("status", "success");
        result.put("code", 200);
        result.put("message", "OK");

        Date endDate = new Date();
        Map<String, Object> data = new HashMap<>();
        data.put("duration", (endDate.getTime() - startDate.getTime()) / 1000);
        data.put("message", applicationName + " is healthy");
        result.put("data", data);

        return result;
    }

}
