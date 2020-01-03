package com.paytm.digital.education.coaching.connections.rest;

import com.google.common.net.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.PAYTM_APP_REQUEST_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.PAYTM_REQUEST_ID;

public class HeaderTemplate {

    private HeaderTemplate() {
    }

    public static Map<String, String> getMerchantHeader(String paytmRequestId,
            String checksumHash, String accessKey, String paytmAppRequestId) {
        return new HashMap<String, String>() {
            private static final long serialVersionUID = -8750656206782040960L;

            {
                put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
                put(PAYTM_REQUEST_ID, paytmRequestId);
                put(PAYTM_APP_REQUEST_ID, paytmAppRequestId);
            }
        };
    }
}
