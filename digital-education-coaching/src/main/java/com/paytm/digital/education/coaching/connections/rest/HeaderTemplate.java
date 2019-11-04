package com.paytm.digital.education.coaching.connections.rest;

import com.google.common.net.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.ACCESS_KEY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.CHECKSUM_HASH;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.PAYTM_REQUEST_ID;

public class HeaderTemplate {

    private HeaderTemplate() {
    }

    public static Map<String, String> getMerchantHeader(String paytmRequestId,
            String checksumHash, String accessKey) {
        return new HashMap<String, String>() {
            private static final long serialVersionUID = -8750656206782040960L;

            {
                put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
                put(PAYTM_REQUEST_ID, paytmRequestId);
                put(CHECKSUM_HASH, checksumHash);
                put(ACCESS_KEY, accessKey);
            }
        };
    }
}
