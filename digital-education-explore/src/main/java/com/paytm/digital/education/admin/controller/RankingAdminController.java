package com.paytm.digital.education.admin.controller;

import com.paytm.digital.education.admin.request.RankingsRequest;
import com.paytm.digital.education.admin.response.RankingResponse;
import com.paytm.digital.education.admin.service.RankingService;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;

@RestController
@RequestMapping(EDUCATION_BASE_URL)
@AllArgsConstructor
@Validated
public class RankingAdminController {

    private static final Logger log = LoggerFactory.getLogger(RankingAdminController.class);

    private RankingService rankingService;

    @PutMapping("/admin/v1/rankings")
    public @ResponseBody RankingResponse updateRankings(
            @RequestBody @Valid
                    RankingsRequest rankingsRequest) {
        return rankingService.updateRankings(rankingsRequest);
    }

    @GetMapping("/admin/v1/rankings")
    public @ResponseBody RankingResponse getRankings(
            @RequestParam(value = "entity") @NotNull EducationEntity entity) {
        return rankingService.getPaytmRankings(entity);
    }
}
