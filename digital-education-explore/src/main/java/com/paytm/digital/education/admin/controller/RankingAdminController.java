package com.paytm.digital.education.admin.controller;

import com.paytm.digital.education.admin.request.RankingsRequest;
import com.paytm.digital.education.admin.response.RankingResponse;
import com.paytm.digital.education.admin.service.RankingService;
import com.paytm.digital.education.explore.enums.EducationEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

@Slf4j
@RestController
@RequestMapping(EDUCATION_BASE_URL)
@AllArgsConstructor
@Validated
public class RankingAdminController {

    private RankingService rankingService;

    @PutMapping("/admin/v1/rankings")
    public @ResponseBody RankingResponse updateRankings(
            @RequestBody @Valid
                    RankingsRequest rankingsRequest) {
        return rankingService.updateRankings(rankingsRequest);
    }

    @GetMapping ("/admin/v1/rankings")
    public @ResponseBody RankingResponse getRankings(@RequestParam(value = "entity")
            @NotNull EducationEntity entity) {
        return rankingService.getPaytmRankings(entity);
    }
}
