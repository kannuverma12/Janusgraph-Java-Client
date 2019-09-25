package com.paytm.digital.education.application.config.security;

import com.paytm.digital.education.constant.ErrorCode;
import com.paytm.digital.education.exception.InvalidRequestException;
import com.paytm.digital.education.utility.CommonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RequestIdInterceptor implements HandlerInterceptor {

    private static Logger log = LoggerFactory.getLogger(RequestIdInterceptor.class);

    private static final String REQUEST_ID = "RequestId";
    private static final String CLIENT_ID = "ClientId";

    private static final String X_REQUEST_ID = "X-REQUEST-ID";
    private static final String X_CLIENT_ID = "X-CLIENT-ID";


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) {
        ContentCachingRequestWrapper requestCacheWrapperObject =
                new ContentCachingRequestWrapper(request);
        requestCacheWrapperObject.getParameterMap();

        if (requestCacheWrapperObject.getHeader(X_CLIENT_ID) == null) {

            log.error("{} header is missing in api {}", X_CLIENT_ID,
                    requestCacheWrapperObject.getRequestURI());
            throw InvalidRequestException.builder()
                    .errorCode(ErrorCode.UNAUTHORIZED_REQUEST)
                    .message(CommonUtils.messageFormat("Header is missing", X_CLIENT_ID))
                    .build();
        }
        String requestId = requestCacheWrapperObject.getHeader(X_REQUEST_ID);
        String clientId = requestCacheWrapperObject.getHeader(X_CLIENT_ID);

        if (StringUtils.isBlank(requestId)) {
            requestId = UUID.randomUUID().toString();
        }

        MDC.put(CLIENT_ID, clientId);
        MDC.put(REQUEST_ID, requestId);

        ThreadContext.put(REQUEST_ID, requestId);
        ThreadContext.put(CLIENT_ID, clientId);

        response.addHeader(REQUEST_ID, requestId);
        response.addHeader(CLIENT_ID, clientId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
        MDC.remove(REQUEST_ID);
        MDC.remove(CLIENT_ID);
        ThreadContext.remove(REQUEST_ID);
        ThreadContext.remove(CLIENT_ID);
    }

}
