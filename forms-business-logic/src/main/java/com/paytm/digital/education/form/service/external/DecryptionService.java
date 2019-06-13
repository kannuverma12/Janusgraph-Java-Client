package com.paytm.digital.education.form.service.external;

import com.paytm.digital.education.form.service.impl.BaseRestApiService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DecryptionService {

    @Autowired
    private BaseRestApiService baseRestApiService;

    @Value("${decryption.service.url}")
    private String decryptionServiceUrl;

    public Long decryptOrderId(String eod) {
        String val = "{\"inputStr\":\"" + eod + "\"}";
        Map<String, String> params = new HashMap<>();
        params.put("options", val);
        String orderIdStr = baseRestApiService.get(decryptionServiceUrl, params, String.class);
        Long orderId = Long.parseLong(orderIdStr);
        return orderId;
    }

}
