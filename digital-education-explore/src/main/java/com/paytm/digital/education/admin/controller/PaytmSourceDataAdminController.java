package com.paytm.digital.education.admin.controller;

import com.paytm.digital.education.admin.request.PaytmSourceDataRequest;
import com.paytm.digital.education.admin.response.GetPaytmSourceDataResponse;
import com.paytm.digital.education.admin.response.PaytmSourceDataResponse;
import com.paytm.digital.education.admin.service.impl.PaytmSourceDataServiceImpl;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;

@RestController
@RequestMapping(EDUCATION_BASE_URL)
@AllArgsConstructor
public class PaytmSourceDataAdminController {

    private static final Logger log = LoggerFactory.getLogger(PaytmSourceDataAdminController.class);

    private PaytmSourceDataServiceImpl paytmSourceDataService;

    @PostMapping("/admin/v1/paytmSourceData")
    public @ResponseBody PaytmSourceDataResponse createPaytmSourceData(
            @RequestBody @Valid PaytmSourceDataRequest paytmSourceDataRequest) {
        return paytmSourceDataService.savePaytmSourceData(paytmSourceDataRequest);
    }

    @GetMapping("/admin/v1/paytmSourceData/{entity}/{entity_id}")
    public @ResponseBody GetPaytmSourceDataResponse getPaytmSourceData(
            @NotNull @PathVariable("entity") EducationEntity entity,
            @PathVariable("entity_id") Long entityId) {
        return paytmSourceDataService.getPaytmSourceData(entity, entityId);
    }

    @DeleteMapping("/admin/v1/paytmSourceData")
    public @ResponseBody PaytmSourceDataResponse deletePaytmSourceData(
            @RequestBody @Valid PaytmSourceDataRequest paytmSourceDataRequest) {
        return paytmSourceDataService.deletePaytmSourceData(paytmSourceDataRequest);
    }

}
