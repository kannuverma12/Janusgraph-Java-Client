package com.paytm.digital.education.form.controller;

import static com.paytm.digital.education.form.constants.FblConstants.INVALID_REFERENCE_ID;
import static com.paytm.digital.education.form.constants.FblConstants.REFERENCE_ID;

import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.service.CollegePredictorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/formfbl/predictor")
@AllArgsConstructor
public class CollegePredictorController {

    private CollegePredictorService collegePredictorService;

    @PostMapping("/v1/form/save")
    public ResponseEntity<Object> saveFormData(@RequestBody FormData formData) {
        Map<String, Object> responseDataMap =
                collegePredictorService.savePredictorFormData(formData);
        if (!responseDataMap.containsKey(REFERENCE_ID)) {
            return new ResponseEntity<>("{\"message\": \"" + INVALID_REFERENCE_ID + "\"}",
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(responseDataMap, HttpStatus.OK);
    }
}
