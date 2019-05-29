package com.paytm.digital.education.form.config;

import com.google.common.base.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;
import paytm.auth.personaaclclient.infrastructure.models.RequestCookiePreAuthenticatedAuthenticationToken;
import paytm.auth.personaaclclient.infrastructure.models.RequestPreAuthenticatedAuthenticationToken;
import paytm.auth.personaaclclient.infrastructure.models.RequestUsernamePasswordAuthenticationToken;
import paytm.auth.personaaclclient.infrastructure.security.AuthenticationFilter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FormAuthenticationFilter extends AuthenticationFilter {
    private final Logger logger = LogManager.getLogger(FormAuthenticationFilter.class);

    private final String cookieName = "ff.sid";
    private final String authenticateApi;
    private AuthenticationManager authenticationManager;

    public FormAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        this.authenticateApi = null;
    }

    public FormAuthenticationFilter(AuthenticationManager authenticationManager, String authenticateApi) {
        super(authenticationManager, authenticateApi);
        this.authenticationManager = authenticationManager;
        this.authenticateApi = authenticateApi;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = asHttp(request);
        HttpServletResponse httpResponse = asHttp(response);

        Optional<String> username = Optional.fromNullable(httpRequest.getHeader("X-Auth-Username"));
        Optional<String> password = Optional.fromNullable(httpRequest.getHeader("X-Auth-Password"));
        Optional<String> tokenHeader = Optional.fromNullable(httpRequest.getHeader("authtoken"));
        Optional<String> token = Optional.fromNullable(httpRequest.getParameter("authtoken"));

        boolean isAuthCookiePresent = WebUtils.getCookie(httpRequest, cookieName) != null;

        String resourcePath = new UrlPathHelper().getPathWithinApplication(httpRequest);

        try {
            if (postToAuthenticate(httpRequest, resourcePath)) {
                logger.debug("Trying to authenticate user {} by X-Auth-Username method", username);
                processUsernamePasswordAuthentication(httpRequest, httpResponse, username, password);
                return;
            }

            if (tokenHeader.isPresent()) {
                logger.debug("Trying to authenticate user by authtoken(in Header) method. Token: {}", tokenHeader);
                processTokenAuthentication(tokenHeader, httpRequest);
            } else if (token.isPresent()) {
                logger.debug("Trying to authenticate user by authtoken method. Token: {}", token);
                processTokenAuthentication(token, httpRequest);
            } else if (isAuthCookiePresent) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Trying to authenticate user by cookie method. Cookie: {}",
                            WebUtils.getCookie(httpRequest, cookieName).getValue());
                }
                processCookieAuthentication(httpRequest);
            }

            logger.debug("FormAuthenticationFilter is passing request down the filter chain");
            // addSessionContextToLogging();
            chain.doFilter(request, response);
        } catch (InternalAuthenticationServiceException internalAuthenticationServiceException) {
            SecurityContextHolder.clearContext();
            logger.error("Internal authentication service exception", internalAuthenticationServiceException);
            httpResponse.sendError(HttpServletResponse.SC_CONFLICT);
        } catch (AuthenticationException authenticationException) {
            SecurityContextHolder.clearContext();
            logger.error("Authentication service exception", authenticationException);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException.getMessage());
        } finally {
            ThreadContext.remove(TOKEN_SESSION_KEY);
            ThreadContext.remove(USER_SESSION_KEY);
        }
    }

    private HttpServletRequest asHttp(ServletRequest request) {
        return (HttpServletRequest) request;
    }

    private HttpServletResponse asHttp(ServletResponse response) {
        return (HttpServletResponse) response;
    }

    private boolean postToAuthenticate(HttpServletRequest httpRequest, String resourcePath) {
        return authenticateApi != null && authenticateApi.equalsIgnoreCase(resourcePath)
                && httpRequest.getMethod().equals("POST");
    }

    private void processUsernamePasswordAuthentication(HttpServletRequest httpRequest,
                                                       HttpServletResponse httpResponse,
                                                       Optional<String> username,
                                                       Optional<String> password) throws IOException {
        Authentication resultOfAuthentication = tryToAuthenticateWithUsernameAndPassword(username, password,
                httpRequest);
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
        httpResponse.setStatus(HttpServletResponse.SC_OK);
        String token = resultOfAuthentication.getDetails().toString();
        httpResponse.addHeader("Content-Type", "application/json");
        httpResponse.getWriter().print(token);
    }

    private Authentication tryToAuthenticateWithUsernameAndPassword(Optional<String> username,
                                                                    Optional<String> password,
                                                                    HttpServletRequest httpRequest) {
        UsernamePasswordAuthenticationToken requestAuthentication =
                new RequestUsernamePasswordAuthenticationToken(username, password, httpRequest);
        return tryToAuthenticate(requestAuthentication);
    }

    private void processTokenAuthentication(Optional<String> token, HttpServletRequest httpRequest) {
        Authentication resultOfAuthentication = tryToAuthenticateWithToken(token, httpRequest);
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
    }

    private void processCookieAuthentication(HttpServletRequest httpRequest) {
        Authentication resultOfAuthentication = tryToAuthenticateWithCookie(httpRequest);
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
    }

    private Authentication tryToAuthenticateWithToken(Optional<String> token, HttpServletRequest httpRequest) {
        PreAuthenticatedAuthenticationToken requestAuthentication = new RequestPreAuthenticatedAuthenticationToken(
                token, null, httpRequest);
        return tryToAuthenticate(requestAuthentication);
    }

    private Authentication tryToAuthenticateWithCookie(HttpServletRequest httpRequest) {
        Authentication requestAuthentication = new RequestCookiePreAuthenticatedAuthenticationToken(null, null,
                httpRequest);
        return tryToAuthenticate(requestAuthentication);
    }

    private Authentication tryToAuthenticate(Authentication requestAuthentication) {
        Authentication responseAuthentication = authenticationManager.authenticate(requestAuthentication);
        if (responseAuthentication == null || !responseAuthentication.isAuthenticated()) {
            throw new InternalAuthenticationServiceException(
                    "Unable to authenticate Domain User for provided credentials");
        }
        logger.debug("User successfully authenticated");
        return responseAuthentication;
    }

}
