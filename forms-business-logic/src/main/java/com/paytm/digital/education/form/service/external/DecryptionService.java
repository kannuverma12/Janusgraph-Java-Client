package com.paytm.digital.education.form.service.external;

import com.paytm.digital.education.constant.ErrorCode;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.exception.DependencyException;
import com.paytm.digital.education.form.service.impl.RestApiService;
import com.paytm.digital.education.mapping.ErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class DecryptionService {

    @Autowired
    private RestApiService baseRestApiService;

    @Value("${decryption.service.url}")
    private String decryptionServiceUrl;

    public Long decryptOrderId(String eod) {
        Map<String, String> params = new HashMap<>();
        params.put("inputStr", eod);
        String orderIdStr = null;
        try {
            orderIdStr = baseRestApiService.get(decryptionServiceUrl, params, String.class);
        } catch (Exception e) {
            log.error("Some error occured in calling decryption service for eod: {} error : {}",
                    eod, e.getMessage());
            throw new DependencyException(e.getCause(), ErrorCode.DP_RESOURCE_ACCESS_EXCEPTION,
                    "Decryption failed", "oms decryption");
        }
        Long orderId = null;
        try {
            orderId = Long.parseLong(orderIdStr);
        } catch (Exception e) {
            log.error("Invalid Eod: {} decrypted orderId: {}", eod, orderIdStr);
            throw new BadRequestException(ErrorEnum.INVALID_EOD,
                    ErrorEnum.INVALID_EOD.getExternalMessage());
        }
        return orderId;
    }

}
