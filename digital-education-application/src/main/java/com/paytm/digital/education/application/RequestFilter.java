package com.paytm.digital.education.application;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.PAYTM_REQUEST_ID;

@Component
public class RequestFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        long startTime = System.currentTimeMillis();
        HttpServletRequest httpReq = (HttpServletRequest) request;
        try {
            String uuid = httpReq.getHeader(PAYTM_REQUEST_ID);
            if (StringUtils.isEmpty(uuid)) {
                uuid = UUID.randomUUID().toString();
            }
            HttpServletResponse httpResp = ((HttpServletResponse) response);
            MDC.put(PAYTM_REQUEST_ID, uuid);
            httpResp.addHeader(PAYTM_REQUEST_ID, uuid);
            chain.doFilter(request, response);
        } finally {
            long endTime = System.currentTimeMillis();
            MDC.clear();
        }
    }

    @Override
    public void destroy() {
    }
}
