package com.paytm.digital.education.form.controller;

import com.paytm.digital.education.form.service.MerchantConfigService;
import com.paytm.digital.education.form.model.MerchantConfiguration;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/v1/form")
@AllArgsConstructor
public class ConfigurationController {

    private MerchantConfigService merchantConfigService;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/getConfig")
    @ResponseBody
    public ResponseEntity<Object> getConfiguration(
            @RequestParam(value = "merchantId") String merchantId,
            @RequestParam(value = "keys", defaultValue = "") ArrayList<String> keys) {

        MerchantConfiguration value = merchantConfigService.getMerchantById(merchantId, keys);

        if (value != null) {
            return new ResponseEntity<>(value, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{\"error\": \"Configuration not found for : " + merchantId + "\"}",
                    HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/saveConfig")
    public String saveConfiguration(@RequestBody MerchantConfiguration merchantConfiguration) {
        merchantConfigService.saveOrUpdateMerchantConfiguration(merchantConfiguration);
        return "{\"message\": \"Details are saved for : " + merchantConfiguration.getMerchantId() + "\"}";
    }

}