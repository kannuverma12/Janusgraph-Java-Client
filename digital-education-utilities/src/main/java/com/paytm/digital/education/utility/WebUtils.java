package com.paytm.digital.education.utility;

import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.experimental.UtilityClass;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;

@UtilityClass
public class WebUtils {

    public static Logger log = LoggerFactory.getLogger(WebUtils.class);

    public static String getFullRequestURI(final HandlerMapping mapping,
                                           final HttpServletRequest request,
                                           final String baseControllerUri) {
        try {
            HandlerExecutionChain chain = mapping.getHandler(request);
            if (chain != null) {
                HandlerMethod handler = (HandlerMethod) chain.getHandler();
                String requestURI = String.join(",", handler.getMethodAnnotation(RequestMapping.class).path());
                return buildURIFromParts(baseControllerUri, requestURI);
            }
        } catch (Exception e) {
            log.warn("Request URI could not be handled - " + request.getRequestURI(), e);
        }
        return request.getRequestURI();
    }

    private String buildURIFromParts(String... parts) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
        Arrays.stream(parts).forEach(uriComponentsBuilder::path);
        return uriComponentsBuilder.build().toUriString();
    }

    private static String getRequestMethodMappingURI(String requestMethod,
            HandlerMethod handlerMethod) {
        switch (requestMethod.toUpperCase()) {
            case "GET":
                return handlerMethod.getMethodAnnotation(GetMapping.class).value()[0];
            case "POST":
                return handlerMethod.getMethodAnnotation(PostMapping.class).value()[0];
            case "PUT":
                return handlerMethod.getMethodAnnotation(PutMapping.class).value()[0];
            case "DELETE":
                return handlerMethod.getMethodAnnotation(DeleteMapping.class).value()[0];
            default:
                return "";
        }
    }
}
