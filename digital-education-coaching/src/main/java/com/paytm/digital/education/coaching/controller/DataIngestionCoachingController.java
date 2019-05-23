package com.paytm.digital.education.coaching.controller;

import com.paytm.digital.education.coaching.data.service.InstituteCRUDService;
import com.paytm.digital.education.coaching.database.entity.CoachingInstitute;
import com.paytm.digital.education.coaching.response.dto.InstituteResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Validated
@RestController
@RequestMapping("/v1/coaching")
@AllArgsConstructor
public class DataIngestionCoachingController {

    private InstituteCRUDService instituteCRUDService;

    @PostMapping("/institute")
    public InstituteResponseDto createInstitute(
            @RequestBody @Valid CoachingInstitute coachingInstitute) {
        return instituteCRUDService.createInstitute(coachingInstitute);
    }

    @PutMapping("/institute")
    public InstituteResponseDto updateInstitute(
            @RequestBody @Valid CoachingInstitute coachingInstitute) {
        return instituteCRUDService.updateInstitute(coachingInstitute);
    }

    @PutMapping("/institute/{instituteId}")
    public Long enableDisableInstitute(@PathVariable @NotNull @Min(1) Long instituteId,
            @NotNull Boolean activate) {
        return instituteCRUDService.updateInstituteStatus(instituteId, activate);
    }

    @PutMapping("/institute/{instituteId}/{centerId}")
    public Long enableDisableCoachingCenter(@PathVariable @NotNull @Min(1) Long instituteId,
            @PathVariable @NotNull @Min(1) Long centerId, @NotNull Boolean activate) {
        return instituteCRUDService.updateCoachingCenterStatus(instituteId, centerId, activate);
    }

    @GetMapping("/institute/{instituteId}")
    public InstituteResponseDto getInstituteById(@PathVariable @NotNull @Min(1) Long instituteId,
            Boolean active) {
        return instituteCRUDService.getInstituteById(instituteId, active);
    }
}
