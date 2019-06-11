package com.paytm.digital.education.form.controller;

import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.model.LatestFormData;
import com.paytm.digital.education.form.service.SaveAndFetchService;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/formfbl/form-data")
@AllArgsConstructor
@Slf4j
public class SaveAndFetchController {

    private SaveAndFetchService saveAndFetchService;

    private final String argMessage =
            "Either refId or combination of customer id, merchant id, candidate id are required";

    @PostMapping("/v1/save")
    public ResponseEntity<Object> saveData(
            @RequestParam(name = "confirm_flag", defaultValue = "false") boolean confirmFlag,
            @RequestBody FormData data) {
        log.info("Save request body : {} with confirm flag :{}", JsonUtils.toJson(data),
                confirmFlag);
        if (!saveAndFetchService.validateFormDataRequest(data)) {
            log.error("Validation failed: " + argMessage);
            return new ResponseEntity<>(
                    "{\"message\": \"" + argMessage + "\"}", HttpStatus.BAD_REQUEST);
        }

        //todo: Add merchant candidate id to case sensitive check on the basis of merchant config
        if (data.getCandidateId() != null) {
            data.setCandidateId(data.getCandidateId().toLowerCase());
            if (data.getCandidateDetails() != null
                    && data.getCandidateDetails().getEmail() != null) {
                data.getCandidateDetails()
                        .setEmail(data.getCandidateDetails().getEmail().toLowerCase());
            }
        }

        String id = saveAndFetchService.saveData(data, confirmFlag);
        if (id != null) {
            log.info("Saved successfully, Reference id: " + id);
            return new ResponseEntity<>("{\"refId\": \"" + id + "\"}", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    "{\"message\": \"Some error occurred, please try again later\"}",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //@PostMapping("/v1/save-addons")
    public ResponseEntity<Object> saveAddonData(@RequestBody FormData data) {
        log.info("Save-addons {}", JsonUtils.toJson(data));
        if (!saveAndFetchService.validateFormDataRequest(data)) {
            log.error("Validation failed: " + argMessage);
            return new ResponseEntity<>("{\"message\": \"" + argMessage + "\"}",
                    HttpStatus.BAD_REQUEST);
        }
        String id = saveAndFetchService.saveDataWithAddon(data);

        if (id != null) {
            log.info("Updated successfully, Reference id: " + id);
            return new ResponseEntity<>("{\"refId\": \"" + id + "\"}", HttpStatus.OK);
        } else {
            log.error("Some error occurred.");
            return new ResponseEntity<>(
                    "{\"message\": \"Some error occurred, please try again later\"}",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/v1/get-form-data")
    public ResponseEntity<Object> getFormData(
            @RequestParam("merchantId") String merchantId,
            @RequestParam("customerId") String customerId,
            @RequestParam("candidateId") String candidateId) {

        FormData formData =
                saveAndFetchService.getLatestRecord(merchantId, customerId, candidateId);

        if (formData == null) {
            return new ResponseEntity<>("{\"message\": \"No record found for the given Ids\" }",
                    HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("{\"form_data\": " + JsonUtils.toJson(formData) + "}",
                    HttpStatus.OK);
        }
    }

    @GetMapping("/v1/get-form-data-by-id")
    public ResponseEntity<Object> getFormData(@RequestParam("refId") String refId) {

        FormData formData = saveAndFetchService.getRecord(refId);

        if (formData == null) {
            return new ResponseEntity<>("{\"message\": \"No record found for the given Ids\" }",
                    HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("{\"form_data\": " + JsonUtils.toJson(formData) + "}",
                    HttpStatus.OK);
        }
    }

    @GetMapping("/v1/form-entries")
    public LatestFormData getCurrentOpenAndLastPaidFormDetails(
            @RequestParam("merchant_id") @NotBlank String merchantId,
            @RequestParam("customer_id") @NotBlank String customerId,
            @RequestParam("candidate_id") @NotBlank String candidateId,
            @RequestParam("keys") @NotNull List<String> keys) {
        return saveAndFetchService
                .getCurrentOpenAndLastPaidFormDetails(merchantId, customerId, candidateId, keys);
    }

}
