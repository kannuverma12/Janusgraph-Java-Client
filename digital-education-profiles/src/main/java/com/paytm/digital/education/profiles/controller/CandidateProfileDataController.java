package com.paytm.digital.education.profiles.controller;

import com.paytm.digital.education.profiles.db.model.ProfileDataEntity;
import com.paytm.digital.education.profiles.request.ProfileDataRequest;
import com.paytm.digital.education.profiles.service.CandidateProfileDataService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@AllArgsConstructor
@RequestMapping("/v1")
public class CandidateProfileDataController {

    private CandidateProfileDataService candidateProfileDataService;

    @PutMapping(value = "/profiles/{profile_id}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileDataEntity> updateCandidateProfileData(
            @PathVariable(value = "profile_id") @NotNull @Min(1) Long profileId,
            @Valid @RequestBody ProfileDataRequest profileDataRequest) {

        ProfileDataEntity candidateInformation = candidateProfileDataService
                .updateCandidateProfileData(profileDataRequest, profileId);
        return new ResponseEntity<>(candidateInformation, HttpStatus.OK);
    }

}
