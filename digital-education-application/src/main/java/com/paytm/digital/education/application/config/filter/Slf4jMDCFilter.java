package com.paytm.digital.education.application.config.filter;

import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


@Data
@Component
@Order(2)
public class Slf4jMDCFilter extends OncePerRequestFilter {

    public static final String PaytmRequestId      = "PaytmRequestId";
    public static final String PaytmClientIdHeader = "CustomerId";
    public static final String PaytmOrderIdHeader  = "OrderId";

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain chain)
            throws java.io.IOException, ServletException {
        try {
            ContentCachingRequestWrapper requestCacheWrapperObject =
                    new ContentCachingRequestWrapper(request);
            requestCacheWrapperObject.getParameterMap();

            String requestId = requestCacheWrapperObject.getHeader(PaytmRequestId);
            if (Strings.isBlank(requestId)) {
                requestId =
                        UUID.randomUUID().toString().toUpperCase().replace("-", "");
            }

            String clientId = requestCacheWrapperObject.getHeader(PaytmClientIdHeader);
            String orderId = requestCacheWrapperObject.getHeader(PaytmOrderIdHeader);

            MDC.put(PaytmClientIdHeader, clientId);
            MDC.put(PaytmRequestId, requestId);
            MDC.put(PaytmOrderIdHeader, orderId);
            response.addHeader(PaytmRequestId, requestId);
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
