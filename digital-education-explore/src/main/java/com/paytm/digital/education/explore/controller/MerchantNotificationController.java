package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.explore.request.dto.notification.MerchantNotificationRequest;
import com.paytm.digital.education.explore.response.dto.notification.MerchantNotificationResponse;
import com.paytm.digital.education.explore.service.impl.MerchantNotificationServiceImpl;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;

@AllArgsConstructor
@RestController
@RequestMapping(EDUCATION_BASE_URL)
@Validated
@Api(value = "Merchant Notification API", description = "Merchant Notification API")
public class MerchantNotificationController {

    private static Logger log = LoggerFactory.getLogger(MerchantNotificationController.class);

    private MerchantNotificationServiceImpl merchantNotificationServiceImpl;

    @RequestMapping(method = RequestMethod.POST, path = "/auth/v1/merchant/notification")
    @ApiOperation(value = "Save Merchant Notification")
    public @ResponseBody MerchantNotificationResponse saveNotification(
            @Valid @RequestBody MerchantNotificationRequest merchantNotificationRequest) {
        log.info("Notification Request : {}", JsonUtils.toJson(merchantNotificationRequest));
        return merchantNotificationServiceImpl.saveNotification(merchantNotificationRequest);
    }

}
