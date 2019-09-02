package com.paytm.digital.education.application.config.metric;

import com.paytm.digital.education.application.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
public class LoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Not providing any default implementation
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        try {
            insertRequestUniquenessIntoMDC(request);
            chain.doFilter(request, response);
        } finally {
            clearMDC();
        }

    }

    private void insertRequestUniquenessIntoMDC(ServletRequest request) {
        String requestId = getRequestUniqueness(request);
        ThreadContext.put(Constant.Request.ID, requestId); // For log4j logger
        MDC.put(Constant.Request.ID, requestId);
    }

    private String getRequestUniqueness(ServletRequest request) {

        String requestId = getRequestId(request);
        String deviceId = getDeviceId(request);

        if (StringUtils.isEmpty(deviceId)) return requestId;

        return requestId + Constant.Request.REQUEST_DEVICE_ID_SEPARATOR + deviceId;
    }

    private String getRequestId(ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestId = StringUtils.isNotEmpty(httpServletRequest.getHeader(Constant.Request.ID))
                            ? httpServletRequest.getHeader(Constant.Request.ID) : request.getParameter(Constant.Request.ID);

        if (StringUtils.isEmpty(requestId)) return UUID.randomUUID().toString();

        return requestId;
    }

    private String getDeviceId(ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String deviceNameAndImei = StringUtils.isNotEmpty(httpServletRequest.getHeader(Constant.Request.DEVICE_IDENTIFIER))
                ? httpServletRequest.getHeader(Constant.Request.DEVICE_IDENTIFIER)
                : request.getParameter(Constant.Request.DEVICE_IDENTIFIER);

        return deviceNameAndImei;
    }

    private void clearMDC() {
        ThreadContext.clearAll();
        MDC.clear();
    }

    @Override
    public void destroy() {
        clearMDC();
    }
}
