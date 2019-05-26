package com.paytm.digital.education.form.controller;

import com.paytm.digital.education.form.model.MerchantProductConfig;
import com.paytm.digital.education.form.service.MerchantProductConfigService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;

@RestController
@RequestMapping("/formfbl")
@AllArgsConstructor
public class MerchantProductConfigController {

    private MerchantProductConfigService merchantProductConfigService;

    @GetMapping("/v1/getMerchantProductConfig")
    @ResponseBody
    public ResponseEntity<Object> getConfig(
            @RequestParam(value = "merchantId") String merchantId,
            @RequestParam(value = "productId") String productId,
            @RequestParam(value = "keys", defaultValue = "") ArrayList<String> keys) {

        MerchantProductConfig value = merchantProductConfigService.getConfig(merchantId, productId, keys);

        if (value != null) {
            return new ResponseEntity<>(value, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{\"error\": \"Configuration not found for " + productId + "\"}",
                    HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/v1/saveMerchantProductConfig")
    public ResponseEntity<Object> saveConfig(@RequestBody MerchantProductConfig merchantProductConfig) {
        boolean saved = merchantProductConfigService.saveConfig(merchantProductConfig);
        if (saved) {
            return new ResponseEntity<>("{\"message\": \"Details saved for "
                    + merchantProductConfig.getProductId() + "\"}", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{\"message\": \"Details not saved for "
                    + merchantProductConfig.getProductId() + "\"}", HttpStatus.BAD_REQUEST);
        }

    }

}