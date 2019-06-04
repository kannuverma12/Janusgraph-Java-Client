package com.paytm.digital.education.form.controller;

import com.paytm.digital.education.form.service.MerchantConfigService;
import com.paytm.digital.education.form.model.MerchantConfiguration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Map;

@RestController
@RequestMapping("/formfbl")
@AllArgsConstructor
@Slf4j
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

    @GetMapping("/v1/post-order-screen-config")
    public ResponseEntity<Object> getPostOrderScreenConfig(
            @RequestParam(value = "merchant_id", required = false) String merchantId,
            @RequestParam(value = "order_id") Long orderId
    ) {
        Map<String, Object> data = merchantConfigService.getPostScreenData(merchantId, orderId);

        if (data == null) {
            log.error("No data found for orderId = " + orderId + " & merchantId " + merchantId);
            return new ResponseEntity<>(
                    "{\"status_code\": 404,"
                            + "\"message\": \"No data for the provided merchant_id, order_id found\"}",
                    HttpStatus.OK
            );
        }

        String formDownloadLink = (String) data.get("form_download_link");
        if (formDownloadLink != null) {
            formDownloadLink += "?order_id=" + orderId + "&type=form";
            data.put("form_download_link", formDownloadLink);
        }

        String invoiceDownloadLink = (String) data.get("invoice_download_link");
        if (invoiceDownloadLink != null) {
            invoiceDownloadLink += "?order_id=" + orderId + "&type=invoice";
            data.put("invoice_download_link", invoiceDownloadLink);
        }
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

}