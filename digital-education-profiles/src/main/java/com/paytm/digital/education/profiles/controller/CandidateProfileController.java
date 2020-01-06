package com.paytm.digital.education.profiles.controller;

import com.paytm.digital.education.profiles.db.model.ProfileIdentifierEntity;
import com.paytm.digital.education.profiles.request.ProfileCreateRequest;
import com.paytm.digital.education.profiles.request.ProfilePatchRequest;
import com.paytm.digital.education.profiles.response.ProfileDataResponse;
import com.paytm.digital.education.profiles.service.CandidateProfileService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/v1")
public class CandidateProfileController {

    private CandidateProfileService candidateProfileService;

    @PostMapping(value = "/profiles", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileIdentifierEntity> createProfile(
            @Valid @RequestBody ProfileCreateRequest profileCreateRequest) {

        ProfileIdentifierEntity profileIdentifierEntity = candidateProfileService.saveCandidateInfo(
                profileCreateRequest);
        return new ResponseEntity<>(profileIdentifierEntity, HttpStatus.OK);
    }

    @GetMapping(value = "/profiles/{profile_id}")
    public ResponseEntity<ProfileDataResponse> getProfileById(
            @PathVariable(value = "profile_id") @NotNull @Min(1) Long profileId,
            @RequestParam(value = "keys", required = false) List<String> keys) {

        ProfileDataResponse profileData = candidateProfileService
                .getCandidateInfo(profileId, keys);
        return new ResponseEntity<>(profileData, HttpStatus.OK);
    }

    @GetMapping(value = "/profiles")
    public ResponseEntity<ProfileDataResponse> getProfilesByCustomerId(
            @RequestParam(value = "customer_id") @NotNull @Min(1) Long customerId) {
        ProfileDataResponse profileDataList =
                candidateProfileService.getCandidatesByCustomerId(customerId);
        return new ResponseEntity<>(profileDataList, HttpStatus.OK);

    }

    @PatchMapping(value = "/profiles/{profile_id}")
    public ResponseEntity<ProfileIdentifierEntity> disableProfile(
            @PathVariable("profile_id") @NotNull @Min(1) Long profileId,
            @Valid @RequestBody ProfilePatchRequest request) {

        ProfileIdentifierEntity profileIdentifierEntity =
                candidateProfileService.disableProfileById(profileId,
                        request.getIsEnabled());
        return new ResponseEntity<>(profileIdentifierEntity, HttpStatus.OK);
    }

}
