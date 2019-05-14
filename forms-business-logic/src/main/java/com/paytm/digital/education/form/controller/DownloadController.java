package com.paytm.digital.education.form.controller;

import com.paytm.digital.education.form.service.DownloadService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/formfbl")
public class DownloadController {

    private DownloadService downloadService;

    @GetMapping("/v1/download")
    public ResponseEntity<Object> downloadFormOrInvoice(
            @RequestParam("id") String id,
            @RequestParam("type") String type) { // todo: change to enum

        if (id != null && type != null
                && (type.equalsIgnoreCase("form") || type.equalsIgnoreCase("invoice"))) {

            HttpHeaders headers = new HttpHeaders();
            String filename = type + "_" + id + ".pdf"; // todo: filename logic should go in function
            headers.setContentDispositionFormData("filename", filename);
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            // todo: choose between either of following based on merchant config
            // byte[] contents = downloadService.getPdfByteArray(id, type);
            byte[] contents = downloadService.getTempAimaResponse(id, type);

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
