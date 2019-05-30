package com.paytm.digital.education.form.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Health {

    @GetMapping("/formfbl/form-health")
    public ResponseEntity health() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
