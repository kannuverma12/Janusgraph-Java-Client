package com.paytm.digital.education.admin.controller;

import com.paytm.digital.education.admin.request.PaytmSourceDataRequest;
import com.paytm.digital.education.admin.response.PaytmSourceDataResponse;
import com.paytm.digital.education.admin.service.impl.PaytmSourceDataServiceImpl;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;

@RestController
@Validated
@RequestMapping(EDUCATION_BASE_URL)
@AllArgsConstructor
public class PaytmEntityDataAdminController {

    private static final Logger log = LoggerFactory.getLogger(PaytmEntityDataAdminController.class);

    private PaytmSourceDataServiceImpl paytmSourceDataService;

    @PostMapping("/admin/v1/paytmSourceData")
    public @ResponseBody PaytmSourceDataResponse createPaytmSourceData(
            @RequestBody @Valid PaytmSourceDataRequest paytmSourceDataRequest) {
        return paytmSourceDataService.createPaytmSourceData(paytmSourceDataRequest);
    }

}
