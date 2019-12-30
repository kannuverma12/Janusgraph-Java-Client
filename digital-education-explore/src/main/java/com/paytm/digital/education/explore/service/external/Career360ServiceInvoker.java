package com.paytm.digital.education.explore.service.external;

import static com.paytm.digital.education.constant.ExploreConstants.CAREER_360_MAX_RETRY;

import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.Map;

@Service
public class Career360ServiceInvoker {

    @Autowired
    private BaseRestApiService restApiService;

    private static final Logger log = LoggerFactory.getLogger(Career360ServiceInvoker.class);

    @Retryable(value = {ResourceAccessException.class}, maxAttempts = CAREER_360_MAX_RETRY)
    public <T> T post(String postUrl, Class<T> responseType, String requestStr,
            Map<String, String> headers) {
        return restApiService.post(postUrl, responseType, requestStr, headers);
    }
}
