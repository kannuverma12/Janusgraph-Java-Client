package com.paytm.digital.education.form.controller;


import com.paytm.digital.education.form.config.AuthorizationService;
import com.paytm.digital.education.form.model.ErrorResponseBody;
import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.model.MerchantConfiguration;
import com.paytm.digital.education.form.service.DownloadService;
import com.paytm.digital.education.form.service.impl.MerchantConfigServiceImpl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/formfbl")
@Slf4j
public class DownloadController {

    private AuthorizationService authService;
    private DownloadService downloadService;
    private MerchantConfigServiceImpl merchantConfigServiceImpl;
    private Environment env;

    @GetMapping("/v1/download")
    public ResponseEntity<Object> downloadFormOrInvoice(
            @RequestParam("order_id") Long orderId,
            @RequestParam("type") String type
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Credentials", "true");

        //  String[] orderIds = orderId.split(",");
        //  if (orderIds.length > 1) {
        //      return new ResponseEntity<>(
        //              "{\"status_code\":400, \"message\": \"Only one orderId are allowed.\"}",headers,
        //              HttpStatus.BAD_REQUEST);
        //  }

        if (authService.getMerchantId() == null) {
            return new ResponseEntity<>(new ErrorResponseBody(400, "User is not merchant"), headers,
                    HttpStatus.BAD_REQUEST);
        }

        String merchantId = authService.getMerchantId().toString();
        FormData formData = downloadService.getFormDataByMerchantIdAndOrderId(merchantId, orderId);
        if (formData == null) {
            return new ResponseEntity<>(
                    new ErrorResponseBody(404, "data not found"), headers,
                    HttpStatus.NOT_FOUND);
        }

        return downloadForm(orderId, type, formData, headers);
    }

    @GetMapping("/auth/v1/user/form/download")
    public ResponseEntity<Object> downloadFormByUser(
            @RequestParam("order_id") Long orderId,
            @RequestParam("type") String type,
            @RequestHeader("x-user-id") String userId
    ) {
        FormData formData = downloadService.getFormDataByUserIdAndOrderId(userId, orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Credentials", "true");

        if (userId == null || formData == null) {

            return new ResponseEntity<>(
                    new ErrorResponseBody(404, "data not found"), headers,
                    HttpStatus.NOT_FOUND);
        }

        return downloadForm(orderId, type, formData, headers);
    }

    @GetMapping("/auth/v1/user/form/downloadLink")
    public ResponseEntity<Object> downloadForm(@RequestHeader("x-user-id") String userId) {
        Map<String, Object> pdfConfig = new HashMap<>();

        pdfConfig.put("url", env.getProperty("downloadpdf.url"));
        HttpHeaders headers = new HttpHeaders();

        String filename = "Form.pdf";
        headers.setContentDispositionFormData("filename", filename);
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");


        byte[] contents = null;
        try {
            contents = downloadService.getTempAimaResponse(null, pdfConfig, userId);
        } catch (Exception ex) {
            log.error("ERROR OCCURRED IN PROCESSING PDF : {}", ex);
        }

        if (contents == null) {
            return new ResponseEntity<>(
                    "{\"status_code\":500, \"message\": \"Some error occurred, please try again later.\"}",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(contents, headers, HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Object> downloadForm(Long orderId, String type, FormData formData, HttpHeaders headers) {

        if (orderId != null && type != null
                && (type.equalsIgnoreCase("form") || type.equalsIgnoreCase("invoice"))) {

            String filename = type + "_" + orderId + ".pdf";
            headers.setContentDispositionFormData("filename", filename);
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            ArrayList<String> keys = new ArrayList<>();
            keys.add("data.isMerchantPdf");
            keys.add("data.pdfConfig");

            MerchantConfiguration merchantConfiguration =
                    merchantConfigServiceImpl.getMerchantById(formData.getMerchantId(), keys);
            Map<String, Object> config = null;

            if (merchantConfiguration != null) {
                config = merchantConfiguration.getData();
            }

            byte[] contents = null;

            try {
                if (config != null && config.get("isMerchantPdf").equals(false)
                        || type.equalsIgnoreCase("invoice")) {
                    contents = downloadService.getPdfByteArray(formData, type);

                } else if (config != null
                        && config.get("isMerchantPdf").equals(true) && config.containsKey("pdfConfig")) {
                    contents = downloadService.getTempAimaResponse(
                            orderId, (Map<String, Object>) config.get("pdfConfig"), formData.getCustomerId());

                } else {
                    contents = downloadService.getPdfByteArray(formData, type);
                }
            } catch (Exception e) {
                log.error("ERROR OCCURRED IN PROCESSING PDF : {}", e);
            }

            if (contents == null) {
                return new ResponseEntity<>(
                        "{\"status_code\":500, \"message\": \"Some error occurred, please try again later.\"}",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(contents, headers, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(
                    "{\"status_code\":400, \"message\": \"Please enter the correct id or type\"}",
                    HttpStatus.BAD_REQUEST);
        }

    }
}
