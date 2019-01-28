package com.paytm.digital.education.application.config.metric;

import com.paytm.digital.education.application.constant.Constant;
import com.paytm.digital.education.utility.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Order(1)
@Component
public class ResponseCodeMetricFilter extends OncePerRequestFilter {

    @Autowired
    private DataDogClient dataDogClient;

    @Autowired
    private HandlerMapping mapping;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);

        String fullRequestURI = WebUtils.getFullRequestURI(mapping, request, Constant.BASE_CONTROLLER_URI);
        String datadogMetricName = request.getMethod() + " " + fullRequestURI;

        HttpStatus httpStatus = HttpStatus.resolve(response.getStatus());
        dataDogClient.recordResponseCodeCount(datadogMetricName, httpStatus);
    }
}
